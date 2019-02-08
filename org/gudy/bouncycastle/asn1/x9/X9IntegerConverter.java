/*    */ package org.gudy.bouncycastle.asn1.x9;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import org.gudy.bouncycastle.math.ec.ECCurve;
/*    */ import org.gudy.bouncycastle.math.ec.ECFieldElement;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class X9IntegerConverter
/*    */ {
/*    */   public int getByteLength(ECCurve c)
/*    */   {
/* 13 */     return (c.getFieldSize() + 7) / 8;
/*    */   }
/*    */   
/*    */ 
/*    */   public int getByteLength(ECFieldElement fe)
/*    */   {
/* 19 */     return (fe.getFieldSize() + 7) / 8;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public byte[] integerToBytes(BigInteger s, int qLength)
/*    */   {
/* 26 */     byte[] bytes = s.toByteArray();
/*    */     
/* 28 */     if (qLength < bytes.length)
/*    */     {
/* 30 */       byte[] tmp = new byte[qLength];
/*    */       
/* 32 */       System.arraycopy(bytes, bytes.length - tmp.length, tmp, 0, tmp.length);
/*    */       
/* 34 */       return tmp;
/*    */     }
/* 36 */     if (qLength > bytes.length)
/*    */     {
/* 38 */       byte[] tmp = new byte[qLength];
/*    */       
/* 40 */       System.arraycopy(bytes, 0, tmp, tmp.length - bytes.length, bytes.length);
/*    */       
/* 42 */       return tmp;
/*    */     }
/*    */     
/* 45 */     return bytes;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x9/X9IntegerConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */