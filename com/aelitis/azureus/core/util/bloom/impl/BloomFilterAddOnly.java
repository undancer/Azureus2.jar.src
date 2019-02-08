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
/*     */ public class BloomFilterAddOnly
/*     */   extends BloomFilterImpl
/*     */ {
/*     */   private final byte[] map;
/*     */   
/*     */   public BloomFilterAddOnly(int _max_entries)
/*     */   {
/*  37 */     super(_max_entries);
/*     */     
/*  39 */     this.map = new byte[(getMaxEntries() + 7) / 8];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public BloomFilterAddOnly(Map<String, Object> x)
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
/*  63 */     return new BloomFilterAddOnly(getMaxEntries());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected int trimValue(int value)
/*     */   {
/*  70 */     if (value < 0)
/*  71 */       return 0;
/*  72 */     if (value > 1) {
/*  73 */       return 1;
/*     */     }
/*  75 */     return value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int getValue(int index)
/*     */   {
/*  83 */     byte b = this.map[(index / 8)];
/*     */     
/*  85 */     return b >> index % 8 & 0x1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int incValue(int index)
/*     */   {
/*  97 */     int original_value = getValue(index);
/*     */     
/*  99 */     if (original_value >= 1)
/*     */     {
/* 101 */       return 1;
/*     */     }
/*     */     
/* 104 */     setValue(index, (byte)(original_value + 1));
/*     */     
/* 106 */     return original_value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int decValue(int index)
/*     */   {
/* 117 */     int original_value = getValue(index);
/*     */     
/* 119 */     if (original_value <= 0)
/*     */     {
/* 121 */       return 0;
/*     */     }
/*     */     
/* 124 */     setValue(index, (byte)(original_value - 1));
/*     */     
/* 126 */     return original_value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setValue(int index, byte value)
/*     */   {
/* 135 */     byte b = this.map[(index / 8)];
/*     */     
/* 137 */     if (value == 0)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 142 */       throw new RuntimeException("remove not supported");
/*     */     }
/*     */     
/*     */ 
/* 146 */     b = (byte)(b | 1 << index % 8);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 151 */     this.map[(index / 8)] = b;
/*     */   }
/*     */   
/*     */ 
/*     */   public void clear()
/*     */   {
/* 157 */     Arrays.fill(this.map, (byte)0);
/*     */     
/* 159 */     super.clear();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/bloom/impl/BloomFilterAddOnly.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */