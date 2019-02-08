/*    */ package org.gudy.bouncycastle.util.encoders;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Hex
/*    */ {
/* 12 */   private static HexTranslator encoder = new HexTranslator();
/*    */   
/* 14 */   private static final byte[] hexTable = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static byte[] encode(byte[] array)
/*    */   {
/* 23 */     return encode(array, 0, array.length);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static byte[] encode(byte[] array, int off, int length)
/*    */   {
/* 31 */     byte[] enc = new byte[length * 2];
/*    */     
/* 33 */     encoder.encode(array, off, length, enc, 0);
/*    */     
/* 35 */     return enc;
/*    */   }
/*    */   
/*    */ 
/*    */   public static byte[] decode(String string)
/*    */   {
/* 41 */     byte[] bytes = new byte[string.length() / 2];
/* 42 */     String buf = string.toLowerCase();
/*    */     
/* 44 */     for (int i = 0; i < buf.length(); i += 2)
/*    */     {
/* 46 */       char left = buf.charAt(i);
/* 47 */       char right = buf.charAt(i + 1);
/* 48 */       int index = i / 2;
/*    */       
/* 50 */       if (left < 'a')
/*    */       {
/* 52 */         bytes[index] = ((byte)(left - '0' << 4));
/*    */       }
/*    */       else
/*    */       {
/* 56 */         bytes[index] = ((byte)(left - 'a' + 10 << 4));
/*    */       }
/* 58 */       if (right < 'a')
/*    */       {
/* 60 */         int tmp92_90 = index; byte[] tmp92_89 = bytes;tmp92_89[tmp92_90] = ((byte)(tmp92_89[tmp92_90] + (byte)(right - '0')));
/*    */       }
/*    */       else
/*    */       {
/* 64 */         int tmp109_107 = index; byte[] tmp109_106 = bytes;tmp109_106[tmp109_107] = ((byte)(tmp109_106[tmp109_107] + (byte)(right - 'a' + 10)));
/*    */       }
/*    */     }
/*    */     
/* 68 */     return bytes;
/*    */   }
/*    */   
/*    */ 
/*    */   public static byte[] decode(byte[] array)
/*    */   {
/* 74 */     byte[] bytes = new byte[array.length / 2];
/*    */     
/* 76 */     encoder.decode(array, 0, array.length, bytes, 0);
/*    */     
/* 78 */     return bytes;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/util/encoders/Hex.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */