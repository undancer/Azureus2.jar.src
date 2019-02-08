/*    */ package org.gudy.bouncycastle.util.encoders;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HexTranslator
/*    */   implements Translator
/*    */ {
/* 11 */   private static final byte[] hexTable = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public int getEncodedBlockSize()
/*    */   {
/* 23 */     return 2;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public int encode(byte[] in, int inOff, int length, byte[] out, int outOff)
/*    */   {
/* 33 */     int i = 0; for (int j = 0; i < length; j += 2)
/*    */     {
/* 35 */       out[(outOff + j)] = hexTable[(in[inOff] >> 4 & 0xF)];
/* 36 */       out[(outOff + j + 1)] = hexTable[(in[inOff] & 0xF)];
/*    */       
/* 38 */       inOff++;i++;
/*    */     }
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 41 */     return length * 2;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public int getDecodedBlockSize()
/*    */   {
/* 50 */     return 1;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public int decode(byte[] in, int inOff, int length, byte[] out, int outOff)
/*    */   {
/* 60 */     int halfLength = length / 2;
/*    */     
/* 62 */     for (int i = 0; i < halfLength; i++)
/*    */     {
/* 64 */       byte left = in[(inOff + i * 2)];
/* 65 */       byte right = in[(inOff + i * 2 + 1)];
/*    */       
/* 67 */       if (left < 97)
/*    */       {
/* 69 */         out[outOff] = ((byte)(left - 48 << 4));
/*    */       }
/*    */       else
/*    */       {
/* 73 */         out[outOff] = ((byte)(left - 97 + 10 << 4));
/*    */       }
/* 75 */       if (right < 97)
/*    */       {
/* 77 */         int tmp87_85 = outOff; byte[] tmp87_83 = out;tmp87_83[tmp87_85] = ((byte)(tmp87_83[tmp87_85] + (byte)(right - 48)));
/*    */       }
/*    */       else
/*    */       {
/* 81 */         int tmp105_103 = outOff; byte[] tmp105_101 = out;tmp105_101[tmp105_103] = ((byte)(tmp105_101[tmp105_103] + (byte)(right - 97 + 10)));
/*    */       }
/*    */       
/* 84 */       outOff++;
/*    */     }
/*    */     
/* 87 */     return halfLength;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/util/encoders/HexTranslator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */