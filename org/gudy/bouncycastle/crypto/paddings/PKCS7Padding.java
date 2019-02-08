/*    */ package org.gudy.bouncycastle.crypto.paddings;
/*    */ 
/*    */ import java.security.SecureRandom;
/*    */ import org.gudy.bouncycastle.crypto.InvalidCipherTextException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PKCS7Padding
/*    */   implements BlockCipherPadding
/*    */ {
/*    */   public void init(SecureRandom random)
/*    */     throws IllegalArgumentException
/*    */   {}
/*    */   
/*    */   public String getPaddingName()
/*    */   {
/* 32 */     return "PKCS7";
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public int addPadding(byte[] in, int inOff)
/*    */   {
/* 43 */     byte code = (byte)(in.length - inOff);
/*    */     
/* 45 */     while (inOff < in.length)
/*    */     {
/* 47 */       in[inOff] = code;
/* 48 */       inOff++;
/*    */     }
/*    */     
/* 51 */     return code;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public int padCount(byte[] in)
/*    */     throws InvalidCipherTextException
/*    */   {
/* 60 */     int count = in[(in.length - 1)] & 0xFF;
/*    */     
/* 62 */     if ((count > in.length) || (count == 0))
/*    */     {
/* 64 */       throw new InvalidCipherTextException("pad block corrupted");
/*    */     }
/*    */     
/* 67 */     for (int i = 1; i <= count; i++)
/*    */     {
/* 69 */       if (in[(in.length - i)] != count)
/*    */       {
/* 71 */         throw new InvalidCipherTextException("pad block corrupted");
/*    */       }
/*    */     }
/*    */     
/* 75 */     return count;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/paddings/PKCS7Padding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */