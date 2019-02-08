/*     */ package org.gudy.bouncycastle.crypto.paddings;
/*     */ 
/*     */ import org.gudy.bouncycastle.crypto.BlockCipher;
/*     */ import org.gudy.bouncycastle.crypto.BufferedBlockCipher;
/*     */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*     */ import org.gudy.bouncycastle.crypto.DataLengthException;
/*     */ import org.gudy.bouncycastle.crypto.InvalidCipherTextException;
/*     */ import org.gudy.bouncycastle.crypto.OutputLengthException;
/*     */ import org.gudy.bouncycastle.crypto.params.ParametersWithRandom;
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
/*     */ public class PaddedBufferedBlockCipher
/*     */   extends BufferedBlockCipher
/*     */ {
/*     */   BlockCipherPadding padding;
/*     */   
/*     */   public PaddedBufferedBlockCipher(BlockCipher cipher, BlockCipherPadding padding)
/*     */   {
/*  35 */     this.cipher = cipher;
/*  36 */     this.padding = padding;
/*     */     
/*  38 */     this.buf = new byte[cipher.getBlockSize()];
/*  39 */     this.bufOff = 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PaddedBufferedBlockCipher(BlockCipher cipher)
/*     */   {
/*  50 */     this(cipher, new PKCS7Padding());
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
/*     */   public void init(boolean forEncryption, CipherParameters params)
/*     */     throws IllegalArgumentException
/*     */   {
/*  67 */     this.forEncryption = forEncryption;
/*     */     
/*  69 */     reset();
/*     */     
/*  71 */     if ((params instanceof ParametersWithRandom))
/*     */     {
/*  73 */       ParametersWithRandom p = (ParametersWithRandom)params;
/*     */       
/*  75 */       this.padding.init(p.getRandom());
/*     */       
/*  77 */       this.cipher.init(forEncryption, p.getParameters());
/*     */     }
/*     */     else
/*     */     {
/*  81 */       this.padding.init(null);
/*     */       
/*  83 */       this.cipher.init(forEncryption, params);
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
/*     */ 
/*     */   public int getOutputSize(int len)
/*     */   {
/*  98 */     int total = len + this.bufOff;
/*  99 */     int leftOver = total % this.buf.length;
/*     */     
/* 101 */     if (leftOver == 0)
/*     */     {
/* 103 */       if (this.forEncryption)
/*     */       {
/* 105 */         return total + this.buf.length;
/*     */       }
/*     */       
/* 108 */       return total;
/*     */     }
/*     */     
/* 111 */     return total - leftOver + this.buf.length;
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
/* 125 */     int total = len + this.bufOff;
/* 126 */     int leftOver = total % this.buf.length;
/*     */     
/* 128 */     if (leftOver == 0)
/*     */     {
/* 130 */       return total - this.buf.length;
/*     */     }
/*     */     
/* 133 */     return total - leftOver;
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
/* 152 */     int resultLen = 0;
/*     */     
/* 154 */     if (this.bufOff == this.buf.length)
/*     */     {
/* 156 */       resultLen = this.cipher.processBlock(this.buf, 0, out, outOff);
/* 157 */       this.bufOff = 0;
/*     */     }
/*     */     
/* 160 */     this.buf[(this.bufOff++)] = in;
/*     */     
/* 162 */     return resultLen;
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
/* 185 */     if (len < 0)
/*     */     {
/* 187 */       throw new IllegalArgumentException("Can't have a negative input length!");
/*     */     }
/*     */     
/* 190 */     int blockSize = getBlockSize();
/* 191 */     int length = getUpdateOutputSize(len);
/*     */     
/* 193 */     if (length > 0)
/*     */     {
/* 195 */       if (outOff + length > out.length)
/*     */       {
/* 197 */         throw new OutputLengthException("output buffer too short");
/*     */       }
/*     */     }
/*     */     
/* 201 */     int resultLen = 0;
/* 202 */     int gapLen = this.buf.length - this.bufOff;
/*     */     
/* 204 */     if (len > gapLen)
/*     */     {
/* 206 */       System.arraycopy(in, inOff, this.buf, this.bufOff, gapLen);
/*     */       
/* 208 */       resultLen += this.cipher.processBlock(this.buf, 0, out, outOff);
/*     */       
/* 210 */       this.bufOff = 0;
/* 211 */       len -= gapLen;
/* 212 */       inOff += gapLen;
/*     */       
/* 214 */       while (len > this.buf.length)
/*     */       {
/* 216 */         resultLen += this.cipher.processBlock(in, inOff, out, outOff + resultLen);
/*     */         
/* 218 */         len -= blockSize;
/* 219 */         inOff += blockSize;
/*     */       }
/*     */     }
/*     */     
/* 223 */     System.arraycopy(in, inOff, this.buf, this.bufOff, len);
/*     */     
/* 225 */     this.bufOff += len;
/*     */     
/* 227 */     return resultLen;
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
/*     */   public int doFinal(byte[] out, int outOff)
/*     */     throws DataLengthException, IllegalStateException, InvalidCipherTextException
/*     */   {
/* 249 */     int blockSize = this.cipher.getBlockSize();
/* 250 */     int resultLen = 0;
/*     */     
/* 252 */     if (this.forEncryption)
/*     */     {
/* 254 */       if (this.bufOff == blockSize)
/*     */       {
/* 256 */         if (outOff + 2 * blockSize > out.length)
/*     */         {
/* 258 */           reset();
/*     */           
/* 260 */           throw new OutputLengthException("output buffer too short");
/*     */         }
/*     */         
/* 263 */         resultLen = this.cipher.processBlock(this.buf, 0, out, outOff);
/* 264 */         this.bufOff = 0;
/*     */       }
/*     */       
/* 267 */       this.padding.addPadding(this.buf, this.bufOff);
/*     */       
/* 269 */       resultLen += this.cipher.processBlock(this.buf, 0, out, outOff + resultLen);
/*     */       
/* 271 */       reset();
/*     */     }
/*     */     else
/*     */     {
/* 275 */       if (this.bufOff == blockSize)
/*     */       {
/* 277 */         resultLen = this.cipher.processBlock(this.buf, 0, this.buf, 0);
/* 278 */         this.bufOff = 0;
/*     */       }
/*     */       else
/*     */       {
/* 282 */         reset();
/*     */         
/* 284 */         throw new DataLengthException("last block incomplete in decryption");
/*     */       }
/*     */       
/*     */       try
/*     */       {
/* 289 */         resultLen -= this.padding.padCount(this.buf);
/*     */         
/* 291 */         System.arraycopy(this.buf, 0, out, outOff, resultLen);
/*     */       }
/*     */       finally
/*     */       {
/* 295 */         reset();
/*     */       }
/*     */     }
/*     */     
/* 299 */     return resultLen;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/paddings/PaddedBufferedBlockCipher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */