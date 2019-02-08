/*     */ package org.gudy.bouncycastle.util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Arrays
/*     */ {
/*     */   public static boolean areEqual(byte[] a, byte[] b)
/*     */   {
/*  17 */     if (a == b)
/*     */     {
/*  19 */       return true;
/*     */     }
/*     */     
/*  22 */     if ((a == null) || (b == null))
/*     */     {
/*  24 */       return false;
/*     */     }
/*     */     
/*  27 */     if (a.length != b.length)
/*     */     {
/*  29 */       return false;
/*     */     }
/*     */     
/*  32 */     for (int i = 0; i != a.length; i++)
/*     */     {
/*  34 */       if (a[i] != b[i])
/*     */       {
/*  36 */         return false;
/*     */       }
/*     */     }
/*     */     
/*  40 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean areEqual(int[] a, int[] b)
/*     */   {
/*  47 */     if (a == b)
/*     */     {
/*  49 */       return true;
/*     */     }
/*     */     
/*  52 */     if ((a == null) || (b == null))
/*     */     {
/*  54 */       return false;
/*     */     }
/*     */     
/*  57 */     if (a.length != b.length)
/*     */     {
/*  59 */       return false;
/*     */     }
/*     */     
/*  62 */     for (int i = 0; i != a.length; i++)
/*     */     {
/*  64 */       if (a[i] != b[i])
/*     */       {
/*  66 */         return false;
/*     */       }
/*     */     }
/*     */     
/*  70 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void fill(byte[] array, byte value)
/*     */   {
/*  77 */     for (int i = 0; i < array.length; i++)
/*     */     {
/*  79 */       array[i] = value;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void fill(long[] array, long value)
/*     */   {
/*  87 */     for (int i = 0; i < array.length; i++)
/*     */     {
/*  89 */       array[i] = value;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void fill(short[] array, short value)
/*     */   {
/*  97 */     for (int i = 0; i < array.length; i++)
/*     */     {
/*  99 */       array[i] = value;
/*     */     }
/*     */   }
/*     */   
/*     */   public static int hashCode(byte[] data)
/*     */   {
/* 105 */     int value = 0;
/*     */     
/* 107 */     if (data != null)
/*     */     {
/* 109 */       for (int i = 0; i != data.length; i++)
/*     */       {
/* 111 */         value ^= (data[i] & 0xFF) << i % 4;
/*     */       }
/*     */     }
/*     */     
/* 115 */     return value;
/*     */   }
/*     */   
/*     */   public static byte[] clone(byte[] data)
/*     */   {
/* 120 */     byte[] copy = new byte[data.length];
/*     */     
/* 122 */     System.arraycopy(data, 0, copy, 0, data.length);
/*     */     
/* 124 */     return copy;
/*     */   }
/*     */   
/*     */   public static int[] clone(int[] data)
/*     */   {
/* 129 */     int[] copy = new int[data.length];
/*     */     
/* 131 */     System.arraycopy(data, 0, copy, 0, data.length);
/*     */     
/* 133 */     return copy;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/util/Arrays.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */