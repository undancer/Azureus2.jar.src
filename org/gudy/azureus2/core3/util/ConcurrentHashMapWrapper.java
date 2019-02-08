/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ public class ConcurrentHashMapWrapper<S, T>
/*     */ {
/*  36 */   private static final Object NULL = new Object();
/*     */   
/*  38 */   private final S S_NULL = NULL;
/*  39 */   private final T T_NULL = NULL;
/*     */   
/*     */ 
/*     */   private final ConcurrentHashMap<S, T> map;
/*     */   
/*     */ 
/*     */   public ConcurrentHashMapWrapper(int initialCapacity)
/*     */   {
/*  47 */     this.map = new ConcurrentHashMap(initialCapacity);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConcurrentHashMapWrapper(int initialCapacity, float loadFactor, int concurrencyLevel)
/*     */   {
/*  56 */     this.map = new ConcurrentHashMap(initialCapacity, loadFactor, concurrencyLevel);
/*     */   }
/*     */   
/*     */ 
/*     */   public ConcurrentHashMapWrapper()
/*     */   {
/*  62 */     this.map = new ConcurrentHashMap();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ConcurrentHashMapWrapper(Map<S, T> init_map)
/*     */   {
/*  69 */     this.map = new ConcurrentHashMap(init_map.size());
/*     */     
/*  71 */     putAll(init_map);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void putAll(Map<S, T> from_map)
/*     */   {
/*  78 */     for (Map.Entry<S, T> entry : from_map.entrySet())
/*     */     {
/*  80 */       S key = entry.getKey();
/*  81 */       T value = entry.getValue();
/*     */       
/*  83 */       if (key == null)
/*     */       {
/*  85 */         key = this.S_NULL;
/*     */       }
/*     */       
/*  88 */       if (value == null)
/*     */       {
/*  90 */         value = this.T_NULL;
/*     */       }
/*     */       
/*  93 */       this.map.put(key, value);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public T put(S key, T value)
/*     */   {
/* 102 */     if (key == null)
/*     */     {
/* 104 */       key = this.S_NULL;
/*     */     }
/*     */     
/* 107 */     if (value == null)
/*     */     {
/* 109 */       value = this.T_NULL;
/*     */     }
/*     */     
/* 112 */     T result = this.map.put(key, value);
/*     */     
/* 114 */     if (result == this.T_NULL)
/*     */     {
/* 116 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 120 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public T get(S key)
/*     */   {
/* 128 */     if (key == null)
/*     */     {
/* 130 */       key = this.S_NULL;
/*     */     }
/*     */     
/* 133 */     T result = this.map.get(key);
/*     */     
/* 135 */     if (result == this.T_NULL)
/*     */     {
/* 137 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 141 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public T remove(S key)
/*     */   {
/* 149 */     if (key == null)
/*     */     {
/* 151 */       key = this.S_NULL;
/*     */     }
/*     */     
/*     */ 
/* 155 */     T result = this.map.remove(key);
/*     */     
/* 157 */     if (result == this.T_NULL)
/*     */     {
/* 159 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 163 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean containsKey(S key)
/*     */   {
/* 171 */     if (key == null)
/*     */     {
/* 173 */       key = this.S_NULL;
/*     */     }
/*     */     
/* 176 */     return this.map.containsKey(key);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Set<S> keySet()
/*     */   {
/* 186 */     Set<S> result = this.map.keySet();
/*     */     
/* 188 */     if (result.contains(this.S_NULL))
/*     */     {
/* 190 */       result.remove(this.S_NULL);
/*     */       
/* 192 */       result.add(null);
/*     */     }
/*     */     
/* 195 */     return Collections.unmodifiableSet(result);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TreeMap<S, T> toTreeMap()
/*     */   {
/* 206 */     TreeMap<S, T> result = new TreeMap();
/*     */     
/* 208 */     for (Map.Entry<S, T> entry : this.map.entrySet())
/*     */     {
/* 210 */       S key = entry.getKey();
/* 211 */       T value = entry.getValue();
/*     */       
/* 213 */       if (key == this.S_NULL)
/*     */       {
/* 215 */         key = null;
/*     */       }
/*     */       
/* 218 */       if (value == this.T_NULL)
/*     */       {
/* 220 */         value = null;
/*     */       }
/*     */       
/* 223 */       result.put(key, value);
/*     */     }
/*     */     
/* 226 */     return result;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/ConcurrentHashMapWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */