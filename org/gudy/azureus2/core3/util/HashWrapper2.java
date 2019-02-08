/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HashWrapper2
/*     */ {
/*     */   private final byte[] hash;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final short offset;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final short length;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final int hash_code;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public HashWrapper2(byte[] hash)
/*     */   {
/*  42 */     this(hash, 0, hash.length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public HashWrapper2(byte[] _hash, int _offset, int _length)
/*     */   {
/*  51 */     if (_offset >= 32767) {
/*  52 */       throw new RuntimeException("Illegal value - offset too large");
/*     */     }
/*     */     
/*  55 */     if (_length >= 32767) {
/*  56 */       throw new RuntimeException("Illegal value - length too large");
/*     */     }
/*     */     
/*  59 */     this.hash = _hash;
/*  60 */     this.offset = ((short)_offset);
/*  61 */     this.length = ((short)_length);
/*     */     
/*  63 */     int hc = 0;
/*     */     
/*  65 */     for (int i = this.offset; i < this.offset + this.length; i++)
/*     */     {
/*  67 */       hc = 31 * hc + this.hash[i];
/*     */     }
/*     */     
/*  70 */     this.hash_code = hc;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getBytes()
/*     */   {
/*  76 */     return this.hash;
/*     */   }
/*     */   
/*     */ 
/*     */   public short getOffset()
/*     */   {
/*  82 */     return this.offset;
/*     */   }
/*     */   
/*     */ 
/*     */   public short getLength()
/*     */   {
/*  88 */     return this.length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final boolean equals(Object o)
/*     */   {
/*  95 */     if (!(o instanceof HashWrapper2))
/*     */     {
/*  97 */       return false;
/*     */     }
/*     */     
/* 100 */     HashWrapper2 other = (HashWrapper2)o;
/*     */     
/* 102 */     if (other.length != this.length)
/*     */     {
/* 104 */       return false;
/*     */     }
/*     */     
/* 107 */     byte[] other_hash = other.hash;
/* 108 */     int other_offset = other.offset;
/*     */     
/* 110 */     for (int i = 0; i < this.length; i++)
/*     */     {
/* 112 */       if (this.hash[(this.offset + i)] != other_hash[(other_offset + i)])
/*     */       {
/* 114 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 118 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 124 */     return this.hash_code;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/HashWrapper2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */