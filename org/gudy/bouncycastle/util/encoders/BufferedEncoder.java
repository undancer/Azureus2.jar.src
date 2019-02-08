/*    */ package org.gudy.bouncycastle.util.encoders;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BufferedEncoder
/*    */ {
/*    */   protected byte[] buf;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected int bufOff;
/*    */   
/*    */ 
/*    */ 
/*    */   protected Translator translator;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public BufferedEncoder(Translator translator, int bufSize)
/*    */   {
/* 25 */     this.translator = translator;
/*    */     
/* 27 */     if (bufSize % translator.getEncodedBlockSize() != 0)
/*    */     {
/* 29 */       throw new IllegalArgumentException("buffer size not multiple of input block size");
/*    */     }
/*    */     
/* 32 */     this.buf = new byte[bufSize];
/* 33 */     this.bufOff = 0;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public int processByte(byte in, byte[] out, int outOff)
/*    */   {
/* 41 */     int resultLen = 0;
/*    */     
/* 43 */     this.buf[(this.bufOff++)] = in;
/*    */     
/* 45 */     if (this.bufOff == this.buf.length)
/*    */     {
/* 47 */       resultLen = this.translator.encode(this.buf, 0, this.buf.length, out, outOff);
/* 48 */       this.bufOff = 0;
/*    */     }
/*    */     
/* 51 */     return resultLen;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff)
/*    */   {
/* 61 */     if (len < 0)
/*    */     {
/* 63 */       throw new IllegalArgumentException("Can't have a negative input length!");
/*    */     }
/*    */     
/* 66 */     int resultLen = 0;
/* 67 */     int gapLen = this.buf.length - this.bufOff;
/*    */     
/* 69 */     if (len > gapLen)
/*    */     {
/* 71 */       System.arraycopy(in, inOff, this.buf, this.bufOff, gapLen);
/*    */       
/* 73 */       resultLen += this.translator.encode(this.buf, 0, this.buf.length, out, outOff);
/*    */       
/* 75 */       this.bufOff = 0;
/*    */       
/* 77 */       len -= gapLen;
/* 78 */       inOff += gapLen;
/* 79 */       outOff += resultLen;
/*    */       
/* 81 */       int chunkSize = len - len % this.buf.length;
/*    */       
/* 83 */       resultLen += this.translator.encode(in, inOff, chunkSize, out, outOff);
/*    */       
/* 85 */       len -= chunkSize;
/* 86 */       inOff += chunkSize;
/*    */     }
/*    */     
/* 89 */     if (len != 0)
/*    */     {
/* 91 */       System.arraycopy(in, inOff, this.buf, this.bufOff, len);
/*    */       
/* 93 */       this.bufOff += len;
/*    */     }
/*    */     
/* 96 */     return resultLen;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/util/encoders/BufferedEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */