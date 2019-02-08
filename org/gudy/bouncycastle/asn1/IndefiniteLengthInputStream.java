/*     */ package org.gudy.bouncycastle.asn1;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ class IndefiniteLengthInputStream
/*     */   extends LimitedInputStream
/*     */ {
/*     */   private int _b1;
/*     */   private int _b2;
/*  11 */   private boolean _eofReached = false;
/*  12 */   private boolean _eofOn00 = true;
/*     */   
/*     */ 
/*     */   IndefiniteLengthInputStream(InputStream in)
/*     */     throws IOException
/*     */   {
/*  18 */     super(in);
/*     */     
/*  20 */     this._b1 = in.read();
/*  21 */     this._b2 = in.read();
/*  22 */     this._eofReached = (this._b2 < 0);
/*     */   }
/*     */   
/*     */ 
/*     */   void setEofOn00(boolean eofOn00)
/*     */   {
/*  28 */     this._eofOn00 = eofOn00;
/*     */   }
/*     */   
/*     */   boolean checkForEof()
/*     */   {
/*  33 */     if ((this._eofOn00) && (this._b1 == 0) && (this._b2 == 0))
/*     */     {
/*  35 */       this._eofReached = true;
/*  36 */       setParentEofDetect(true);
/*     */     }
/*  38 */     return this._eofReached;
/*     */   }
/*     */   
/*     */ 
/*     */   public int read(byte[] b, int off, int len)
/*     */     throws IOException
/*     */   {
/*  45 */     if ((this._eofOn00) || (len < 3))
/*     */     {
/*  47 */       return super.read(b, off, len);
/*     */     }
/*     */     
/*  50 */     if (this._eofReached)
/*     */     {
/*  52 */       return -1;
/*     */     }
/*     */     
/*  55 */     int numRead = this._in.read(b, off + 2, len - 2);
/*     */     
/*  57 */     if (numRead < 0)
/*     */     {
/*     */ 
/*  60 */       this._eofReached = true;
/*  61 */       return -1;
/*     */     }
/*     */     
/*  64 */     b[off] = ((byte)this._b1);
/*  65 */     b[(off + 1)] = ((byte)this._b2);
/*     */     
/*  67 */     this._b1 = this._in.read();
/*  68 */     this._b2 = this._in.read();
/*     */     
/*  70 */     if (this._b2 < 0)
/*     */     {
/*     */ 
/*     */ 
/*  74 */       this._eofReached = true;
/*     */     }
/*     */     
/*     */ 
/*  78 */     return numRead + 2;
/*     */   }
/*     */   
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*  84 */     if (checkForEof())
/*     */     {
/*  86 */       return -1;
/*     */     }
/*     */     
/*  89 */     int b = this._in.read();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  95 */     if (b < 0)
/*     */     {
/*     */ 
/*     */ 
/*  99 */       this._eofReached = true;
/*     */       
/* 101 */       return -1;
/*     */     }
/*     */     
/* 104 */     int v = this._b1;
/*     */     
/* 106 */     this._b1 = this._b2;
/* 107 */     this._b2 = b;
/*     */     
/* 109 */     return v;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/IndefiniteLengthInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */