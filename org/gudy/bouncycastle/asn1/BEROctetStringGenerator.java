/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class BEROctetStringGenerator
/*    */   extends BERGenerator
/*    */ {
/*    */   public BEROctetStringGenerator(OutputStream out)
/*    */     throws IOException
/*    */   {
/* 12 */     super(out);
/*    */     
/* 14 */     writeBERHeader(36);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public BEROctetStringGenerator(OutputStream out, int tagNo, boolean isExplicit)
/*    */     throws IOException
/*    */   {
/* 23 */     super(out, tagNo, isExplicit);
/*    */     
/* 25 */     writeBERHeader(36);
/*    */   }
/*    */   
/*    */   public OutputStream getOctetOutputStream()
/*    */   {
/* 30 */     return getOctetOutputStream(new byte['Ϩ']);
/*    */   }
/*    */   
/*    */ 
/*    */   public OutputStream getOctetOutputStream(byte[] buf)
/*    */   {
/* 36 */     return new BufferedBEROctetStream(buf);
/*    */   }
/*    */   
/*    */ 
/*    */   private class BufferedBEROctetStream
/*    */     extends OutputStream
/*    */   {
/*    */     private byte[] _buf;
/*    */     private int _off;
/*    */     
/*    */     BufferedBEROctetStream(byte[] buf)
/*    */     {
/* 48 */       this._buf = buf;
/* 49 */       this._off = 0;
/*    */     }
/*    */     
/*    */ 
/*    */     public void write(int b)
/*    */       throws IOException
/*    */     {
/* 56 */       this._buf[(this._off++)] = ((byte)b);
/*    */       
/* 58 */       if (this._off == this._buf.length)
/*    */       {
/* 60 */         BEROctetStringGenerator.this._out.write(new DEROctetString(this._buf).getEncoded());
/* 61 */         this._off = 0;
/*    */       }
/*    */     }
/*    */     
/*    */     public void write(byte[] b, int off, int len) throws IOException
/*    */     {
/* 67 */       while (len > 0)
/*    */       {
/* 69 */         int numToCopy = Math.min(len, this._buf.length - this._off);
/* 70 */         System.arraycopy(b, off, this._buf, this._off, numToCopy);
/*    */         
/* 72 */         this._off += numToCopy;
/* 73 */         if (this._off < this._buf.length) {
/*    */           break;
/*    */         }
/*    */         
/*    */ 
/* 78 */         BEROctetStringGenerator.this._out.write(new DEROctetString(this._buf).getEncoded());
/* 79 */         this._off = 0;
/*    */         
/* 81 */         off += numToCopy;
/* 82 */         len -= numToCopy;
/*    */       }
/*    */     }
/*    */     
/*    */     public void close()
/*    */       throws IOException
/*    */     {
/* 89 */       if (this._off != 0)
/*    */       {
/* 91 */         byte[] bytes = new byte[this._off];
/* 92 */         System.arraycopy(this._buf, 0, bytes, 0, this._off);
/*    */         
/* 94 */         BEROctetStringGenerator.this._out.write(new DEROctetString(bytes).getEncoded());
/*    */       }
/*    */       
/* 97 */       BEROctetStringGenerator.this.writeBEREnd();
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/BEROctetStringGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */