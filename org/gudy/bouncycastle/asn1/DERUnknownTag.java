/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DERUnknownTag
/*    */   extends DERObject
/*    */ {
/*    */   int tag;
/*    */   byte[] data;
/*    */   
/*    */   public DERUnknownTag(int tag, byte[] data)
/*    */   {
/* 22 */     this.tag = tag;
/* 23 */     this.data = data;
/*    */   }
/*    */   
/*    */   public int getTag()
/*    */   {
/* 28 */     return this.tag;
/*    */   }
/*    */   
/*    */   public byte[] getData()
/*    */   {
/* 33 */     return this.data;
/*    */   }
/*    */   
/*    */ 
/*    */   void encode(DEROutputStream out)
/*    */     throws IOException
/*    */   {
/* 40 */     out.writeEncoded(this.tag, this.data);
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 46 */     if (!(o instanceof DERUnknownTag))
/*    */     {
/* 48 */       return false;
/*    */     }
/*    */     
/* 51 */     DERUnknownTag other = (DERUnknownTag)o;
/*    */     
/* 53 */     if (this.tag != other.tag)
/*    */     {
/* 55 */       return false;
/*    */     }
/*    */     
/* 58 */     if (this.data.length != other.data.length)
/*    */     {
/* 60 */       return false;
/*    */     }
/*    */     
/* 63 */     for (int i = 0; i < this.data.length; i++)
/*    */     {
/* 65 */       if (this.data[i] != other.data[i])
/*    */       {
/* 67 */         return false;
/*    */       }
/*    */     }
/*    */     
/* 71 */     return true;
/*    */   }
/*    */   
/*    */   public int hashCode()
/*    */   {
/* 76 */     byte[] b = getData();
/* 77 */     int value = 0;
/*    */     
/* 79 */     for (int i = 0; i != b.length; i++)
/*    */     {
/* 81 */       value ^= (b[i] & 0xFF) << i % 4;
/*    */     }
/*    */     
/* 84 */     return value ^ getTag();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERUnknownTag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */