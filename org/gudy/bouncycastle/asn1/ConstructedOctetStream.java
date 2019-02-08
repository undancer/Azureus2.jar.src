/*     */ package org.gudy.bouncycastle.asn1;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ 
/*     */ class ConstructedOctetStream
/*     */   extends InputStream
/*     */ {
/*     */   private final ASN1ObjectParser _parser;
/*  11 */   private boolean _first = true;
/*     */   
/*     */   private InputStream _currentStream;
/*     */   
/*     */   ConstructedOctetStream(ASN1ObjectParser parser)
/*     */   {
/*  17 */     this._parser = parser;
/*     */   }
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException
/*     */   {
/*  22 */     if (this._currentStream == null)
/*     */     {
/*  24 */       if (!this._first)
/*     */       {
/*  26 */         return -1;
/*     */       }
/*     */       
/*  29 */       ASN1OctetStringParser s = (ASN1OctetStringParser)this._parser.readObject();
/*     */       
/*  31 */       if (s == null)
/*     */       {
/*  33 */         return -1;
/*     */       }
/*     */       
/*  36 */       this._first = false;
/*  37 */       this._currentStream = s.getOctetStream();
/*     */     }
/*     */     
/*  40 */     int totalRead = 0;
/*     */     
/*     */     for (;;)
/*     */     {
/*  44 */       int numRead = this._currentStream.read(b, off + totalRead, len - totalRead);
/*     */       
/*  46 */       if (numRead >= 0)
/*     */       {
/*  48 */         totalRead += numRead;
/*     */         
/*  50 */         if (totalRead == len)
/*     */         {
/*  52 */           return totalRead;
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/*  57 */         ASN1OctetStringParser aos = (ASN1OctetStringParser)this._parser.readObject();
/*     */         
/*  59 */         if (aos == null)
/*     */         {
/*  61 */           this._currentStream = null;
/*  62 */           return totalRead < 1 ? -1 : totalRead;
/*     */         }
/*     */         
/*  65 */         this._currentStream = aos.getOctetStream();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*  73 */     if (this._currentStream == null)
/*     */     {
/*  75 */       if (!this._first)
/*     */       {
/*  77 */         return -1;
/*     */       }
/*     */       
/*  80 */       ASN1OctetStringParser s = (ASN1OctetStringParser)this._parser.readObject();
/*     */       
/*  82 */       if (s == null)
/*     */       {
/*  84 */         return -1;
/*     */       }
/*     */       
/*  87 */       this._first = false;
/*  88 */       this._currentStream = s.getOctetStream();
/*     */     }
/*     */     
/*     */     for (;;)
/*     */     {
/*  93 */       int b = this._currentStream.read();
/*     */       
/*  95 */       if (b >= 0)
/*     */       {
/*  97 */         return b;
/*     */       }
/*     */       
/* 100 */       ASN1OctetStringParser s = (ASN1OctetStringParser)this._parser.readObject();
/*     */       
/* 102 */       if (s == null)
/*     */       {
/* 104 */         this._currentStream = null;
/* 105 */         return -1;
/*     */       }
/*     */       
/* 108 */       this._currentStream = s.getOctetStream();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/ConstructedOctetStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */