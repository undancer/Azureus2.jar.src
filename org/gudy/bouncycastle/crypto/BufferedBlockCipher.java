/*     */ package org.gudy.bouncycastle.crypto;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BufferedBlockCipher
/*     */ {
/*     */   protected byte[] buf;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int bufOff;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean forEncryption;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected BlockCipher cipher;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean partialBlockOkay;
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean pgpCFB;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected BufferedBlockCipher() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public BufferedBlockCipher(BlockCipher cipher)
/*     */   {
/*  45 */     this.cipher = cipher;
/*     */     
/*  47 */     this.buf = new byte[cipher.getBlockSize()];
/*  48 */     this.bufOff = 0;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  53 */     String name = cipher.getAlgorithmName();
/*  54 */     int idx = name.indexOf('/') + 1;
/*     */     
/*  56 */     this.pgpCFB = ((idx > 0) && (name.startsWith("PGP", idx)));
/*     */     
/*  58 */     if (this.pgpCFB)
/*     */     {
/*  60 */       this.partialBlockOkay = true;
/*     */     }
/*     */     else
/*     */     {
/*  64 */       this.partialBlockOkay = ((idx > 0) && ((name.startsWith("CFB", idx)) || (name.startsWith("OFB", idx)) || (name.startsWith("OpenPGP", idx)) || (name.startsWith("SIC", idx)) || (name.startsWith("GCTR", idx))));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BlockCipher getUnderlyingCipher()
/*     */   {
/*  75 */     return this.cipher;
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
/*  92 */     this.forEncryption = forEncryption;
/*     */     
/*  94 */     reset();
/*     */     
/*  96 */     this.cipher.init(forEncryption, params);
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
/*     */   public int getUpdateOutputSize(int len)
/*     */   {
/* 120 */     int total = len + this.bufOff;
/*     */     int leftOver;
/*     */     int leftOver;
/* 123 */     if (this.pgpCFB)
/*     */     {
/* 125 */       leftOver = total % this.buf.length - (this.cipher.getBlockSize() + 2);
/*     */     }
/*     */     else
/*     */     {
/* 129 */       leftOver = total % this.buf.length;
/*     */     }
/*     */     
/* 132 */     return total - leftOver;
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
/* 146 */     int total = len + this.bufOff;
/*     */     int leftOver;
/*     */     int leftOver;
/* 149 */     if (this.pgpCFB)
/*     */     {
/* 151 */       leftOver = total % this.buf.length - (this.cipher.getBlockSize() + 2);
/*     */     }
/*     */     else
/*     */     {
/* 155 */       leftOver = total % this.buf.length;
/* 156 */       if (leftOver == 0)
/*     */       {
/* 158 */         return total;
/*     */       }
/*     */     }
/*     */     
/* 162 */     return total - leftOver + this.buf.length;
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
/* 181 */     int resultLen = 0;
/*     */     
/* 183 */     this.buf[(this.bufOff++)] = in;
/*     */     
/* 185 */     if (this.bufOff == this.buf.length)
/*     */     {
/* 187 */       resultLen = this.cipher.processBlock(this.buf, 0, out, outOff);
/* 188 */       this.bufOff = 0;
/*     */     }
/*     */     
/* 191 */     return resultLen;
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
/* 214 */     if (len < 0)
/*     */     {
/* 216 */       throw new IllegalArgumentException("Can't have a negative input length!");
/*     */     }
/*     */     
/* 219 */     int blockSize = getBlockSize();
/* 220 */     int length = getUpdateOutputSize(len);
/*     */     
/* 222 */     if (length > 0)
/*     */     {
/* 224 */       if (outOff + length > out.length)
/*     */       {
/* 226 */         throw new DataLengthException("output buffer too short");
/*     */       }
/*     */     }
/*     */     
/* 230 */     int resultLen = 0;
/* 231 */     int gapLen = this.buf.length - this.bufOff;
/*     */     
/* 233 */     if (len > gapLen)
/*     */     {
/* 235 */       System.arraycopy(in, inOff, this.buf, this.bufOff, gapLen);
/*     */       
/* 237 */       resultLen += this.cipher.processBlock(this.buf, 0, out, outOff);
/*     */       
/* 239 */       this.bufOff = 0;
/* 240 */       len -= gapLen;
/* 241 */       inOff += gapLen;
/*     */       
/* 243 */       while (len > this.buf.length)
/*     */       {
/* 245 */         resultLen += this.cipher.processBlock(in, inOff, out, outOff + resultLen);
/*     */         
/* 247 */         len -= blockSize;
/* 248 */         inOff += blockSize;
/*     */       }
/*     */     }
/*     */     
/* 252 */     System.arraycopy(in, inOff, this.buf, this.bufOff, len);
/*     */     
/* 254 */     this.bufOff += len;
/*     */     
/* 256 */     if (this.bufOff == this.buf.length)
/*     */     {
/* 258 */       resultLen += this.cipher.processBlock(this.buf, 0, out, outOff + resultLen);
/* 259 */       this.bufOff = 0;
/*     */     }
/*     */     
/* 262 */     return resultLen;
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
/* 284 */     int resultLen = 0;
/*     */     
/* 286 */     if (outOff + this.bufOff > out.length)
/*     */     {
/* 288 */       throw new DataLengthException("output buffer too short for doFinal()");
/*     */     }
/*     */     
/* 291 */     if ((this.bufOff != 0) && (this.partialBlockOkay))
/*     */     {
/* 293 */       this.cipher.processBlock(this.buf, 0, this.buf, 0);
/* 294 */       resultLen = this.bufOff;
/* 295 */       this.bufOff = 0;
/* 296 */       System.arraycopy(this.buf, 0, out, outOff, resultLen);
/*     */     }
/* 298 */     else if (this.bufOff != 0)
/*     */     {
/* 300 */       throw new DataLengthException("data not block size aligned");
/*     */     }
/*     */     
/* 303 */     reset();
/*     */     
/* 305 */     return resultLen;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 317 */     for (int i = 0; i < this.buf.length; i++)
/*     */     {
/* 319 */       this.buf[i] = 0;
/*     */     }
/*     */     
/* 322 */     this.bufOff = 0;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 327 */     this.cipher.reset();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/BufferedBlockCipher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */