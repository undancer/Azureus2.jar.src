/*    */ package com.aelitis.azureus.core.util;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class HashCodeUtils
/*    */ {
/*    */   public static final int hashMore(int hash, int more)
/*    */   {
/* 30 */     int result = hash << 1;
/* 31 */     if (result < 0)
/* 32 */       result |= 0x1;
/* 33 */     return result ^ more;
/*    */   }
/*    */   
/*    */   public static final int hashMore(int hash, long more)
/*    */   {
/* 38 */     int result = hashMore(hash, (int)(more >>> 32));
/* 39 */     return hashMore(result, (int)(more & 0xFFFF));
/*    */   }
/*    */   
/*    */   public static final int hashMore(int hash, boolean[] more)
/*    */   {
/* 44 */     int result = hash << 1;
/* 45 */     if (result < 0)
/* 46 */       result |= 0x1;
/* 47 */     if (more[0] != 0)
/* 48 */       result ^= 0x1;
/* 49 */     for (int i = 1; i < more.length; i++)
/*    */     {
/* 51 */       result <<= 1;
/* 52 */       if (result < 0)
/* 53 */         result |= 0x1;
/* 54 */       if (more[i] != 0)
/* 55 */         result ^= 0x1;
/*    */     }
/* 57 */     return result;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final int hashCode(byte[] array)
/*    */   {
/* 65 */     int hash = 0;
/* 66 */     for (int i = 0; i < array.length; i++) {
/* 67 */       hash += array[i];
/* 68 */       hash += (hash << 10);
/* 69 */       hash ^= hash >> 6;
/*    */     }
/* 71 */     hash += (hash << 3);
/* 72 */     hash ^= hash >> 11;
/* 73 */     hash += (hash << 15);
/* 74 */     return hash;
/*    */   }
/*    */   
/*    */   public static final int hashCode(char[] array)
/*    */   {
/* 79 */     int h = 0;
/* 80 */     int len = array.length;
/* 81 */     for (int i = 0; i < len; i++) {
/* 82 */       h = 31 * h + array[i];
/*    */     }
/* 84 */     return h;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/HashCodeUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */