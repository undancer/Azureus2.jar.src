/*     */ package com.aelitis.azureus.core.util.bloom.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.bloom.BloomFilter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ 
/*     */ 
/*     */ public class BloomFilterRotator
/*     */   implements BloomFilter
/*     */ {
/*     */   private volatile BloomFilter current_filter;
/*     */   private int current_filter_index;
/*     */   private final BloomFilter[] filters;
/*  41 */   private long start_time = SystemTime.getMonotonousTime();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public BloomFilterRotator(BloomFilter _target, int _num)
/*     */   {
/*  48 */     this.filters = new BloomFilter[_num];
/*     */     
/*  50 */     this.filters[0] = _target;
/*     */     
/*  52 */     for (int i = 1; i < this.filters.length; i++)
/*     */     {
/*  54 */       this.filters[i] = _target.getReplica();
/*     */     }
/*     */     
/*  57 */     this.current_filter = _target;
/*  58 */     this.current_filter_index = 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public BloomFilterRotator(Map<String, Object> x)
/*     */   {
/*  65 */     List<Map<String, Object>> list = (List)x.get("list");
/*     */     
/*  67 */     this.filters = new BloomFilter[list.size()];
/*     */     
/*  69 */     for (int i = 0; i < this.filters.length; i++)
/*     */     {
/*  71 */       this.filters[i] = BloomFilterImpl.deserialiseFromMap((Map)list.get(i));
/*     */     }
/*     */     
/*  74 */     this.current_filter_index = ((Long)x.get("index")).intValue();
/*     */     
/*  76 */     this.current_filter = this.filters[this.current_filter_index];
/*     */   }
/*     */   
/*     */ 
/*     */   public Map<String, Object> serialiseToMap()
/*     */   {
/*  82 */     Map<String, Object> m = new HashMap();
/*     */     
/*  84 */     serialiseToMap(m);
/*     */     
/*  86 */     return m;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void serialiseToMap(Map<String, Object> x)
/*     */   {
/*  93 */     synchronized (this.filters)
/*     */     {
/*  95 */       String cla = getClass().getName();
/*     */       
/*  97 */       if (cla.startsWith("com.aelitis.azureus.core.util.bloom.impl"))
/*     */       {
/*  99 */         cla = cla.substring("com.aelitis.azureus.core.util.bloom.impl".length());
/*     */       }
/*     */       
/* 102 */       x.put("_impl", cla);
/*     */       
/* 104 */       List<Map<String, Object>> list = new ArrayList();
/*     */       
/* 106 */       for (BloomFilter filter : this.filters)
/*     */       {
/* 108 */         list.add(filter.serialiseToMap());
/*     */       }
/*     */       
/* 111 */       x.put("list", list);
/* 112 */       x.put("index", new Long(this.current_filter_index));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int add(byte[] value)
/*     */   {
/* 120 */     synchronized (this.filters)
/*     */     {
/* 122 */       int filter_size = this.current_filter.getSize();
/* 123 */       int filter_entries = this.current_filter.getEntryCount();
/*     */       
/* 125 */       int limit = filter_size / 8;
/*     */       
/* 127 */       if (filter_entries > limit)
/*     */       {
/* 129 */         filter_entries = limit;
/*     */       }
/*     */       
/* 132 */       int update_chunk = limit / this.filters.length;
/*     */       
/* 134 */       int num_to_update = filter_entries / update_chunk + 1;
/*     */       
/* 136 */       if (num_to_update > this.filters.length)
/*     */       {
/* 138 */         num_to_update = this.filters.length;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 143 */       int res = 0;
/*     */       
/* 145 */       for (int i = this.current_filter_index; i < this.current_filter_index + num_to_update; i++)
/*     */       {
/* 147 */         int r = this.filters[(i % this.filters.length)].add(value);
/*     */         
/* 149 */         if (i == this.current_filter_index)
/*     */         {
/* 151 */           res = r;
/*     */         }
/*     */       }
/*     */       
/* 155 */       if (this.current_filter.getEntryCount() > limit)
/*     */       {
/* 157 */         this.filters[this.current_filter_index] = this.current_filter.getReplica();
/*     */         
/* 159 */         this.current_filter_index = ((this.current_filter_index + 1) % this.filters.length);
/*     */         
/* 161 */         this.current_filter = this.filters[this.current_filter_index];
/*     */       }
/*     */       
/* 164 */       return res;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int remove(byte[] value)
/*     */   {
/* 172 */     int res = 0;
/*     */     
/* 174 */     for (int i = 0; i < this.filters.length; i++)
/*     */     {
/* 176 */       BloomFilter filter = this.filters[i];
/*     */       
/* 178 */       int r = filter.remove(value);
/*     */       
/* 180 */       if (filter == this.current_filter)
/*     */       {
/* 182 */         res = r;
/*     */       }
/*     */     }
/*     */     
/* 186 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean contains(byte[] value)
/*     */   {
/* 193 */     return this.current_filter.contains(value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int count(byte[] value)
/*     */   {
/* 200 */     return this.current_filter.count(value);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getEntryCount()
/*     */   {
/* 206 */     return this.current_filter.getEntryCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSize()
/*     */   {
/* 212 */     return this.current_filter.getSize();
/*     */   }
/*     */   
/*     */ 
/*     */   public BloomFilter getReplica()
/*     */   {
/* 218 */     return new BloomFilterRotator(this.current_filter, this.filters.length);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getStartTimeMono()
/*     */   {
/* 224 */     return this.start_time;
/*     */   }
/*     */   
/*     */ 
/*     */   public void clear()
/*     */   {
/* 230 */     this.start_time = SystemTime.getMonotonousTime();
/*     */     
/* 232 */     for (BloomFilter filter : this.filters)
/*     */     {
/* 234 */       filter.clear();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 241 */     return "ind=" + this.current_filter_index + ",filt=" + this.current_filter.getString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/bloom/impl/BloomFilterRotator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */