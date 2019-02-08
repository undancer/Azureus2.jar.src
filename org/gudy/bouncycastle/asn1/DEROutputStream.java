/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.FilterOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class DEROutputStream
/*    */   extends FilterOutputStream
/*    */   implements DERTags
/*    */ {
/*    */   public DEROutputStream(OutputStream os)
/*    */   {
/* 13 */     super(os);
/*    */   }
/*    */   
/*    */ 
/*    */   private void writeLength(int length)
/*    */     throws IOException
/*    */   {
/* 20 */     if (length > 127)
/*    */     {
/* 22 */       int size = 1;
/* 23 */       int val = length;
/*    */       
/* 25 */       while (val >>>= 8 != 0)
/*    */       {
/* 27 */         size++;
/*    */       }
/*    */       
/* 30 */       write((byte)(size | 0x80));
/*    */       
/* 32 */       for (int i = (size - 1) * 8; i >= 0; i -= 8)
/*    */       {
/* 34 */         write((byte)(length >> i));
/*    */       }
/*    */     }
/*    */     else
/*    */     {
/* 39 */       write((byte)length);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   void writeEncoded(int tag, byte[] bytes)
/*    */     throws IOException
/*    */   {
/* 48 */     write(tag);
/* 49 */     writeLength(bytes.length);
/* 50 */     write(bytes);
/*    */   }
/*    */   
/*    */   protected void writeNull()
/*    */     throws IOException
/*    */   {
/* 56 */     write(5);
/* 57 */     write(0);
/*    */   }
/*    */   
/*    */   public void write(byte[] buf)
/*    */     throws IOException
/*    */   {
/* 63 */     this.out.write(buf, 0, buf.length);
/*    */   }
/*    */   
/*    */   public void write(byte[] buf, int offSet, int len)
/*    */     throws IOException
/*    */   {
/* 69 */     this.out.write(buf, offSet, len);
/*    */   }
/*    */   
/*    */ 
/*    */   public void writeObject(Object obj)
/*    */     throws IOException
/*    */   {
/* 76 */     if (obj == null)
/*    */     {
/* 78 */       writeNull();
/*    */     }
/* 80 */     else if ((obj instanceof DERObject))
/*    */     {
/* 82 */       ((DERObject)obj).encode(this);
/*    */     }
/* 84 */     else if ((obj instanceof DEREncodable))
/*    */     {
/* 86 */       ((DEREncodable)obj).getDERObject().encode(this);
/*    */     }
/*    */     else
/*    */     {
/* 90 */       throw new IOException("object not DEREncodable");
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DEROutputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */