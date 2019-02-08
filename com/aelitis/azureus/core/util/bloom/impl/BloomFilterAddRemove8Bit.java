/*     */ package com.aelitis.azureus.core.util.bloom.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.bloom.BloomFilter;
/*     */ import java.util.Arrays;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ public class BloomFilterAddRemove8Bit
/*     */   extends BloomFilterImpl
/*     */ {
/*     */   private final byte[] map;
/*     */   
/*     */   public BloomFilterAddRemove8Bit(int _max_entries)
/*     */   {
/*  37 */     super(_max_entries);
/*     */     
/*  39 */     this.map = new byte[getMaxEntries()];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public BloomFilterAddRemove8Bit(Map<String, Object> x)
/*     */   {
/*  46 */     super(x);
/*     */     
/*  48 */     this.map = ((byte[])x.get("map"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void serialiseToMap(Map<String, Object> x)
/*     */   {
/*  55 */     super.serialiseToMap(x);
/*     */     
/*  57 */     x.put("map", this.map.clone());
/*     */   }
/*     */   
/*     */ 
/*     */   public BloomFilter getReplica()
/*     */   {
/*  63 */     return new BloomFilterAddRemove8Bit(getMaxEntries());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected int trimValue(int value)
/*     */   {
/*  70 */     if (value < 0)
/*  71 */       return 0;
/*  72 */     if (value > 255) {
/*  73 */       return 255;
/*     */     }
/*  75 */     return value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int getValue(int index)
/*     */   {
/*  83 */     return this.map[index] & 0xFF;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected int incValue(int index)
/*     */   {
/*  90 */     int original_value = getValue(index);
/*     */     
/*  92 */     if (original_value >= 255)
/*     */     {
/*  94 */       return 255;
/*     */     }
/*     */     
/*  97 */     setValue(index, (byte)(original_value + 1));
/*     */     
/*  99 */     return original_value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected int decValue(int index)
/*     */   {
/* 106 */     int original_value = getValue(index);
/*     */     
/* 108 */     if (original_value <= 0)
/*     */     {
/* 110 */       return 0;
/*     */     }
/*     */     
/* 113 */     setValue(index, (byte)(original_value - 1));
/*     */     
/* 115 */     return original_value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setValue(int index, byte value)
/*     */   {
/* 123 */     this.map[index] = value;
/*     */   }
/*     */   
/*     */ 
/*     */   public void clear()
/*     */   {
/* 129 */     Arrays.fill(this.map, (byte)0);
/*     */     
/* 131 */     super.clear();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/bloom/impl/BloomFilterAddRemove8Bit.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */