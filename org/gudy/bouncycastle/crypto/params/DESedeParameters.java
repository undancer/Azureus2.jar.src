/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DESedeParameters
/*    */   extends DESParameters
/*    */ {
/*    */   public static final int DES_EDE_KEY_LENGTH = 24;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public DESedeParameters(byte[] key)
/*    */   {
/* 16 */     super(key);
/*    */     
/* 18 */     if (isWeakKey(key, 0, 0))
/*    */     {
/* 20 */       throw new IllegalArgumentException("attempt to create weak DESede key");
/*    */     }
/*    */   }
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
/*    */   public static boolean isWeakKey(byte[] key, int offset, int length)
/*    */   {
/* 36 */     for (int i = offset; i < length; i += 8)
/*    */     {
/* 38 */       if (DESParameters.isWeakKey(key, i))
/*    */       {
/* 40 */         return true;
/*    */       }
/*    */     }
/*    */     
/* 44 */     return false;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static boolean isWeakKey(byte[] key, int offset)
/*    */   {
/* 57 */     return isWeakKey(key, offset, key.length - offset);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/DESedeParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */