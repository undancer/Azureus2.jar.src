/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.EOFException;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ 
/*    */ 
/*    */ class DefiniteLengthInputStream
/*    */   extends LimitedInputStream
/*    */ {
/*    */   private int _length;
/*    */   
/*    */   DefiniteLengthInputStream(InputStream in, int length)
/*    */   {
/* 16 */     super(in);
/*    */     
/* 18 */     if (length < 0)
/*    */     {
/* 20 */       throw new IllegalArgumentException("negative lengths not allowed");
/*    */     }
/*    */     
/* 23 */     this._length = length;
/*    */   }
/*    */   
/*    */   public int read()
/*    */     throws IOException
/*    */   {
/* 29 */     if (this._length > 0)
/*    */     {
/* 31 */       int b = this._in.read();
/*    */       
/* 33 */       if (b < 0)
/*    */       {
/* 35 */         throw new EOFException();
/*    */       }
/*    */       
/* 38 */       this._length -= 1;
/* 39 */       return b;
/*    */     }
/*    */     
/* 42 */     setParentEofDetect(true);
/*    */     
/* 44 */     return -1;
/*    */   }
/*    */   
/*    */   public int read(byte[] buf, int off, int len)
/*    */     throws IOException
/*    */   {
/* 50 */     if (this._length > 0)
/*    */     {
/* 52 */       int toRead = Math.min(len, this._length);
/* 53 */       int numRead = this._in.read(buf, off, toRead);
/*    */       
/* 55 */       if (numRead < 0) {
/* 56 */         throw new EOFException();
/*    */       }
/* 58 */       this._length -= numRead;
/* 59 */       return numRead;
/*    */     }
/*    */     
/* 62 */     setParentEofDetect(true);
/*    */     
/* 64 */     return -1;
/*    */   }
/*    */   
/*    */   byte[] toByteArray()
/*    */     throws IOException
/*    */   {
/* 70 */     byte[] bytes = new byte[this._length];
/*    */     
/* 72 */     if (this._length > 0)
/*    */     {
/* 74 */       int pos = 0;
/*    */       do
/*    */       {
/* 77 */         int read = this._in.read(bytes, pos, this._length - pos);
/*    */         
/* 79 */         if (read < 0)
/*    */         {
/* 81 */           throw new EOFException();
/*    */         }
/*    */         
/* 84 */         pos += read;
/*    */       }
/* 86 */       while (pos < this._length);
/*    */       
/* 88 */       this._length = 0;
/*    */     }
/*    */     
/* 91 */     setParentEofDetect(true);
/*    */     
/* 93 */     return bytes;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DefiniteLengthInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */