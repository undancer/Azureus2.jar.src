/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class BERGenerator
/*    */   extends ASN1Generator
/*    */ {
/* 10 */   private boolean _tagged = false;
/*    */   
/*    */   private boolean _isExplicit;
/*    */   private int _tagNo;
/*    */   
/*    */   protected BERGenerator(OutputStream out)
/*    */   {
/* 17 */     super(out);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public BERGenerator(OutputStream out, int tagNo, boolean isExplicit)
/*    */   {
/* 25 */     super(out);
/*    */     
/* 27 */     this._tagged = true;
/* 28 */     this._isExplicit = isExplicit;
/* 29 */     this._tagNo = tagNo;
/*    */   }
/*    */   
/*    */   public OutputStream getRawOutputStream()
/*    */   {
/* 34 */     return this._out;
/*    */   }
/*    */   
/*    */ 
/*    */   private void writeHdr(int tag)
/*    */     throws IOException
/*    */   {
/* 41 */     this._out.write(tag);
/* 42 */     this._out.write(128);
/*    */   }
/*    */   
/*    */ 
/*    */   protected void writeBERHeader(int tag)
/*    */     throws IOException
/*    */   {
/* 49 */     if (this._tagged)
/*    */     {
/* 51 */       int tagNum = this._tagNo | 0x80;
/*    */       
/* 53 */       if (this._isExplicit)
/*    */       {
/* 55 */         writeHdr(tagNum | 0x20);
/* 56 */         writeHdr(tag);
/*    */ 
/*    */ 
/*    */       }
/* 60 */       else if ((tag & 0x20) != 0)
/*    */       {
/* 62 */         writeHdr(tagNum | 0x20);
/*    */       }
/*    */       else
/*    */       {
/* 66 */         writeHdr(tagNum);
/*    */       }
/*    */       
/*    */     }
/*    */     else
/*    */     {
/* 72 */       writeHdr(tag);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   protected void writeBERBody(InputStream contentStream)
/*    */     throws IOException
/*    */   {
/*    */     int ch;
/*    */     
/* 82 */     while ((ch = contentStream.read()) >= 0)
/*    */     {
/* 84 */       this._out.write(ch);
/*    */     }
/*    */   }
/*    */   
/*    */   protected void writeBEREnd()
/*    */     throws IOException
/*    */   {
/* 91 */     this._out.write(0);
/* 92 */     this._out.write(0);
/*    */     
/* 94 */     if ((this._tagged) && (this._isExplicit))
/*    */     {
/* 96 */       this._out.write(0);
/* 97 */       this._out.write(0);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/BERGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */