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
/*     */ public class BloomFilterAddRemove4Bit
/*     */   extends BloomFilterImpl
/*     */ {
/*     */   private final byte[] map;
/*     */   
/*     */   public BloomFilterAddRemove4Bit(int _max_entries)
/*     */   {
/*  37 */     super(_max_entries);
/*     */     
/*     */ 
/*     */ 
/*  41 */     this.map = new byte[(getMaxEntries() + 1) / 2];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public BloomFilterAddRemove4Bit(Map<String, Object> x)
/*     */   {
/*  48 */     super(x);
/*     */     
/*  50 */     this.map = ((byte[])x.get("map"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void serialiseToMap(Map<String, Object> x)
/*     */   {
/*  57 */     super.serialiseToMap(x);
/*     */     
/*  59 */     x.put("map", this.map.clone());
/*     */   }
/*     */   
/*     */ 
/*     */   public BloomFilter getReplica()
/*     */   {
/*  65 */     return new BloomFilterAddRemove4Bit(getMaxEntries());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected int trimValue(int value)
/*     */   {
/*  72 */     if (value < 0)
/*  73 */       return 0;
/*  74 */     if (value > 15) {
/*  75 */       return 15;
/*     */     }
/*  77 */     return value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int getValue(int index)
/*     */   {
/*  85 */     byte b = this.map[(index / 2)];
/*     */     
/*  87 */     if (index % 2 == 0)
/*     */     {
/*  89 */       return b & 0xF & 0xFF;
/*     */     }
/*     */     
/*  92 */     return b >> 4 & 0xF & 0xFF;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int incValue(int index)
/*     */   {
/* 100 */     int original_value = getValue(index);
/*     */     
/* 102 */     if (original_value >= 15)
/*     */     {
/* 104 */       return 15;
/*     */     }
/*     */     
/* 107 */     setValue(index, (byte)(original_value + 1));
/*     */     
/* 109 */     return original_value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected int decValue(int index)
/*     */   {
/* 116 */     int original_value = getValue(index);
/*     */     
/* 118 */     if (original_value <= 0)
/*     */     {
/* 120 */       return 0;
/*     */     }
/*     */     
/* 123 */     setValue(index, (byte)(original_value - 1));
/*     */     
/* 125 */     return original_value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setValue(int index, byte value)
/*     */   {
/* 133 */     byte b = this.map[(index / 2)];
/*     */     
/* 135 */     if (index % 2 == 0)
/*     */     {
/* 137 */       b = (byte)(b & 0xF0 | value);
/*     */     }
/*     */     else
/*     */     {
/* 141 */       b = (byte)(b & 0xF | value << 4 & 0xF0);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 146 */     this.map[(index / 2)] = b;
/*     */   }
/*     */   
/*     */ 
/*     */   public void clear()
/*     */   {
/* 152 */     Arrays.fill(this.map, (byte)0);
/*     */     
/* 154 */     super.clear();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/bloom/impl/BloomFilterAddRemove4Bit.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */