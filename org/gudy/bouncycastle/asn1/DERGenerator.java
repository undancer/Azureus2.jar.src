/*     */ package org.gudy.bouncycastle.asn1;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ public abstract class DERGenerator
/*     */   extends ASN1Generator
/*     */ {
/*  11 */   private boolean _tagged = false;
/*     */   
/*     */   private boolean _isExplicit;
/*     */   private int _tagNo;
/*     */   
/*     */   protected DERGenerator(OutputStream out)
/*     */   {
/*  18 */     super(out);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERGenerator(OutputStream out, int tagNo, boolean isExplicit)
/*     */   {
/*  26 */     super(out);
/*     */     
/*  28 */     this._tagged = true;
/*  29 */     this._isExplicit = isExplicit;
/*  30 */     this._tagNo = tagNo;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void writeLength(OutputStream out, int length)
/*     */     throws IOException
/*     */   {
/*  38 */     if (length > 127)
/*     */     {
/*  40 */       int size = 1;
/*  41 */       int val = length;
/*     */       
/*  43 */       while (val >>>= 8 != 0)
/*     */       {
/*  45 */         size++;
/*     */       }
/*     */       
/*  48 */       out.write((byte)(size | 0x80));
/*     */       
/*  50 */       for (int i = (size - 1) * 8; i >= 0; i -= 8)
/*     */       {
/*  52 */         out.write((byte)(length >> i));
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*  57 */       out.write((byte)length);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void writeDEREncoded(OutputStream out, int tag, byte[] bytes)
/*     */     throws IOException
/*     */   {
/*  67 */     out.write(tag);
/*  68 */     writeLength(out, bytes.length);
/*  69 */     out.write(bytes);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   void writeDEREncoded(int tag, byte[] bytes)
/*     */     throws IOException
/*     */   {
/*  77 */     if (this._tagged)
/*     */     {
/*  79 */       int tagNum = this._tagNo | 0x80;
/*     */       
/*  81 */       if (this._isExplicit)
/*     */       {
/*  83 */         int newTag = this._tagNo | 0x20 | 0x80;
/*     */         
/*  85 */         ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/*     */         
/*  87 */         writeDEREncoded(bOut, tag, bytes);
/*     */         
/*  89 */         writeDEREncoded(this._out, newTag, bOut.toByteArray());
/*     */ 
/*     */ 
/*     */       }
/*  93 */       else if ((tag & 0x20) != 0)
/*     */       {
/*  95 */         writeDEREncoded(this._out, tagNum | 0x20, bytes);
/*     */       }
/*     */       else
/*     */       {
/*  99 */         writeDEREncoded(this._out, tagNum, bytes);
/*     */       }
/*     */       
/*     */     }
/*     */     else
/*     */     {
/* 105 */       writeDEREncoded(this._out, tag, bytes);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void writeDEREncoded(OutputStream out, int tag, InputStream in)
/*     */     throws IOException
/*     */   {
/* 115 */     out.write(tag);
/*     */     
/* 117 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/*     */     
/* 119 */     int b = 0;
/* 120 */     while ((b = in.read()) >= 0)
/*     */     {
/* 122 */       bOut.write(b);
/*     */     }
/*     */     
/* 125 */     byte[] bytes = bOut.toByteArray();
/*     */     
/* 127 */     writeLength(out, bytes.length);
/* 128 */     out.write(bytes);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */