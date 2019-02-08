/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public class ByteArrayHashMap<T>
/*     */ {
/*     */   static final int DEFAULT_INITIAL_CAPACITY = 16;
/*     */   static final int MAXIMUM_CAPACITY = 1073741824;
/*     */   static final float DEFAULT_LOAD_FACTOR = 0.75F;
/*     */   protected Entry<T>[] table;
/*     */   protected int size;
/*     */   private int threshold;
/*     */   final float loadFactor;
/*     */   
/*     */   public ByteArrayHashMap(int initialCapacity, float loadFactor)
/*     */   {
/*  59 */     if (initialCapacity < 0) {
/*  60 */       throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
/*     */     }
/*  62 */     if (initialCapacity > 1073741824)
/*  63 */       initialCapacity = 1073741824;
/*  64 */     if ((loadFactor <= 0.0F) || (Float.isNaN(loadFactor))) {
/*  65 */       throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
/*     */     }
/*     */     
/*     */ 
/*  69 */     int capacity = 1;
/*  70 */     while (capacity < initialCapacity) {
/*  71 */       capacity <<= 1;
/*     */     }
/*  73 */     this.loadFactor = loadFactor;
/*  74 */     this.threshold = ((int)(capacity * loadFactor));
/*  75 */     this.table = new Entry[capacity];
/*     */   }
/*     */   
/*     */   public ByteArrayHashMap(int initialCapacity)
/*     */   {
/*  80 */     this(initialCapacity, 0.75F);
/*     */   }
/*     */   
/*     */   public ByteArrayHashMap()
/*     */   {
/*  85 */     this.loadFactor = 0.75F;
/*  86 */     this.threshold = 12;
/*  87 */     this.table = new Entry[16];
/*     */   }
/*     */   
/*     */ 
/*     */   public int size()
/*     */   {
/*  93 */     return this.size;
/*     */   }
/*     */   
/*     */   public boolean isEmpty()
/*     */   {
/*  98 */     return this.size == 0;
/*     */   }
/*     */   
/*     */   public T get(byte[] key, int offset, int len)
/*     */   {
/* 103 */     byte[] k = new byte[len];
/* 104 */     System.arraycopy(key, offset, k, 0, len);
/* 105 */     return (T)get(k);
/*     */   }
/*     */   
/*     */   public T get(byte[] key)
/*     */   {
/* 110 */     int hash = hash(key);
/* 111 */     int i = indexFor(hash, this.table.length);
/* 112 */     Entry<T> e = this.table[i];
/*     */     for (;;) {
/* 114 */       if (e == null)
/* 115 */         return null;
/* 116 */       if ((e.hash == hash) && (eq(key, e.key)))
/* 117 */         return (T)e.value;
/* 118 */       e = e.next;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean containsKey(byte[] key)
/*     */   {
/* 126 */     int hash = hash(key);
/* 127 */     int i = indexFor(hash, this.table.length);
/* 128 */     Entry<T> e = this.table[i];
/*     */     for (;;) {
/* 130 */       if (e == null)
/* 131 */         return false;
/* 132 */       if ((e.hash == hash) && (eq(key, e.key)))
/* 133 */         return true;
/* 134 */       e = e.next;
/*     */     }
/*     */   }
/*     */   
/*     */   public T put(byte[] key, T value) {
/* 139 */     int hash = hash(key);
/* 140 */     int i = indexFor(hash, this.table.length);
/*     */     
/* 142 */     for (Entry<T> e = this.table[i]; e != null; e = e.next) {
/* 143 */       if ((e.hash == hash) && (eq(key, e.key))) {
/* 144 */         T oldValue = e.value;
/* 145 */         e.value = value;
/*     */         
/* 147 */         return oldValue;
/*     */       }
/*     */     }
/*     */     
/* 151 */     addEntry(hash, key, value, i);
/* 152 */     return null;
/*     */   }
/*     */   
/*     */   public T remove(byte[] key)
/*     */   {
/* 157 */     Entry<T> e = removeEntryForKey(key);
/* 158 */     return e == null ? null : e.value;
/*     */   }
/*     */   
/*     */ 
/*     */   public void clear()
/*     */   {
/* 164 */     Entry<T>[] tab = this.table;
/* 165 */     for (int i = 0; i < tab.length; i++)
/* 166 */       tab[i] = null;
/* 167 */     this.size = 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public List<byte[]> keys()
/*     */   {
/* 173 */     List<byte[]> res = new ArrayList();
/*     */     
/* 175 */     for (int j = 0; j < this.table.length; j++) {
/* 176 */       Entry<T> e = this.table[j];
/* 177 */       while (e != null) {
/* 178 */         res.add(e.key);
/*     */         
/* 180 */         e = e.next;
/*     */       }
/*     */     }
/*     */     
/* 184 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public List<T> values()
/*     */   {
/* 190 */     List<T> res = new ArrayList();
/*     */     
/* 192 */     for (int j = 0; j < this.table.length; j++) {
/* 193 */       Entry<T> e = this.table[j];
/* 194 */       while (e != null) {
/* 195 */         res.add(e.value);
/*     */         
/* 197 */         e = e.next;
/*     */       }
/*     */     }
/*     */     
/* 201 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ByteArrayHashMap<T> duplicate()
/*     */   {
/* 212 */     ByteArrayHashMap<T> res = new ByteArrayHashMap(this.size, this.loadFactor);
/*     */     
/* 214 */     for (int j = 0; j < this.table.length; j++) {
/* 215 */       Entry<T> e = this.table[j];
/* 216 */       while (e != null) {
/* 217 */         res.put(e.key, e.value);
/*     */         
/* 219 */         e = e.next;
/*     */       }
/*     */     }
/*     */     
/* 223 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   void resize(int newCapacity)
/*     */   {
/* 230 */     Entry<T>[] oldTable = this.table;
/* 231 */     int oldCapacity = oldTable.length;
/* 232 */     if (oldCapacity == 1073741824) {
/* 233 */       this.threshold = Integer.MAX_VALUE;
/* 234 */       return;
/*     */     }
/*     */     
/* 237 */     Entry<T>[] newTable = new Entry[newCapacity];
/* 238 */     transfer(newTable);
/* 239 */     this.table = newTable;
/* 240 */     this.threshold = ((int)(newCapacity * this.loadFactor));
/*     */   }
/*     */   
/*     */   void transfer(Entry<T>[] newTable)
/*     */   {
/* 245 */     Entry<T>[] src = this.table;
/* 246 */     int newCapacity = newTable.length;
/* 247 */     for (int j = 0; j < src.length; j++) {
/* 248 */       Entry<T> e = src[j];
/* 249 */       if (e != null) {
/* 250 */         src[j] = null;
/*     */         do {
/* 252 */           Entry<T> next = e.next;
/* 253 */           int i = indexFor(e.hash, newCapacity);
/* 254 */           e.next = newTable[i];
/* 255 */           newTable[i] = e;
/* 256 */           e = next;
/* 257 */         } while (e != null);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   Entry<T> removeEntryForKey(byte[] key)
/*     */   {
/* 264 */     int hash = hash(key);
/* 265 */     int i = indexFor(hash, this.table.length);
/* 266 */     Entry<T> prev = this.table[i];
/* 267 */     Entry<T> e = prev;
/*     */     
/* 269 */     while (e != null) {
/* 270 */       Entry<T> next = e.next;
/* 271 */       if ((e.hash == hash) && (eq(key, e.key)))
/*     */       {
/* 273 */         this.size -= 1;
/* 274 */         if (prev == e) {
/* 275 */           this.table[i] = next;
/*     */         } else {
/* 277 */           prev.next = next;
/*     */         }
/* 279 */         return e;
/*     */       }
/* 281 */       prev = e;
/* 282 */       e = next;
/*     */     }
/*     */     
/* 285 */     return e;
/*     */   }
/*     */   
/*     */ 
/*     */   protected static class Entry<S>
/*     */   {
/*     */     public final byte[] key;
/*     */     
/*     */     public S value;
/*     */     
/*     */     public final int hash;
/*     */     public Entry<S> next;
/*     */     
/*     */     Entry(int h, byte[] k, S v, Entry<S> n)
/*     */     {
/* 300 */       this.value = v;
/* 301 */       this.next = n;
/* 302 */       this.key = k;
/* 303 */       this.hash = h;
/*     */     }
/*     */     
/*     */     public byte[] getKey() {
/* 307 */       return this.key;
/*     */     }
/*     */     
/*     */     public S getValue() {
/* 311 */       return (S)this.value;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   void addEntry(int hash, byte[] key, T value, int bucketIndex)
/*     */   {
/* 318 */     this.table[bucketIndex] = new Entry(hash, key, value, this.table[bucketIndex]);
/* 319 */     if (this.size++ >= this.threshold) {
/* 320 */       resize(2 * this.table.length);
/*     */     }
/*     */   }
/*     */   
/*     */   void createEntry(int hash, byte[] key, T value, int bucketIndex) {
/* 325 */     this.table[bucketIndex] = new Entry(hash, key, value, this.table[bucketIndex]);
/* 326 */     this.size += 1;
/*     */   }
/*     */   
/*     */ 
/*     */   private static final int hash(byte[] x)
/*     */   {
/* 332 */     int hash = 0;
/*     */     
/* 334 */     int len = x.length;
/*     */     
/* 336 */     for (int i = 0; i < len; i++)
/*     */     {
/* 338 */       hash = 31 * hash + x[i];
/*     */     }
/*     */     
/* 341 */     return hash;
/*     */   }
/*     */   
/*     */ 
/*     */   private static final boolean eq(byte[] x, byte[] y)
/*     */   {
/* 347 */     if (x == y) {
/* 348 */       return true;
/*     */     }
/*     */     
/* 351 */     int len = x.length;
/*     */     
/* 353 */     if (len != y.length) {
/* 354 */       return false;
/*     */     }
/*     */     
/* 357 */     for (int i = 0; i < len; i++) {
/* 358 */       if (x[i] != y[i]) {
/* 359 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 363 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   private static final int indexFor(int h, int length)
/*     */   {
/* 369 */     return h & length - 1;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/ByteArrayHashMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */