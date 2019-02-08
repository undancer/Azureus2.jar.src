/*    */ package com.aelitis.azureus.core.util.bloom;
/*    */ 
/*    */ import com.aelitis.azureus.core.util.bloom.impl.BloomFilterAddOnly;
/*    */ import com.aelitis.azureus.core.util.bloom.impl.BloomFilterAddRemove4Bit;
/*    */ import com.aelitis.azureus.core.util.bloom.impl.BloomFilterAddRemove8Bit;
/*    */ import com.aelitis.azureus.core.util.bloom.impl.BloomFilterImpl;
/*    */ import com.aelitis.azureus.core.util.bloom.impl.BloomFilterRotator;
/*    */ import java.util.Map;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BloomFilterFactory
/*    */ {
/*    */   public static BloomFilter createAddRemove4Bit(int filter_size)
/*    */   {
/* 45 */     return new BloomFilterAddRemove4Bit(filter_size);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static BloomFilter createAddRemove8Bit(int filter_size)
/*    */   {
/* 52 */     return new BloomFilterAddRemove8Bit(filter_size);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static BloomFilter createAddOnly(int filter_size)
/*    */   {
/* 59 */     return new BloomFilterAddOnly(filter_size);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static BloomFilter createRotating(BloomFilter basis, int number)
/*    */   {
/* 68 */     return new BloomFilterRotator(basis, number);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static BloomFilter deserialiseFromMap(Map<String, Object> map)
/*    */   {
/* 76 */     return BloomFilterImpl.deserialiseFromMap(map);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/bloom/BloomFilterFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */