/*    */ package org.gudy.azureus2.core3.util;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import org.gudy.azureus2.plugins.utils.ByteArrayWrapper;
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
/*    */ 
/*    */ public class HashWrapper
/*    */   implements ByteArrayWrapper
/*    */ {
/*    */   private final byte[] hash;
/*    */   private int hash_code;
/*    */   
/*    */   public HashWrapper(byte[] _hash)
/*    */   {
/* 38 */     this(_hash, 0, _hash.length);
/*    */   }
/*    */   
/*    */   public HashWrapper(byte[] _hash, int offset, int length)
/*    */   {
/* 43 */     this.hash = new byte[length];
/*    */     
/* 45 */     System.arraycopy(_hash, offset, this.hash, 0, length);
/*    */     
/* 47 */     for (int i = 0; i < length; i++)
/*    */     {
/* 49 */       this.hash_code = (31 * this.hash_code + this.hash[i]);
/*    */     }
/*    */   }
/*    */   
/*    */   public boolean equals(Object o) {
/* 54 */     if (!(o instanceof HashWrapper)) {
/* 55 */       return false;
/*    */     }
/* 57 */     byte[] otherHash = ((HashWrapper)o).getHash();
/* 58 */     return Arrays.equals(this.hash, otherHash);
/*    */   }
/*    */   
/*    */ 
/*    */   public byte[] getHash()
/*    */   {
/* 64 */     return this.hash;
/*    */   }
/*    */   
/*    */ 
/*    */   public byte[] getBytes()
/*    */   {
/* 70 */     return this.hash;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 78 */     return this.hash_code;
/*    */   }
/*    */   
/*    */   public String toBase32String() {
/* 82 */     return Base32.encode(this.hash);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/HashWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */