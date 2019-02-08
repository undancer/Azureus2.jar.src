/*     */ package org.apache.commons.lang;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class IntHashMap
/*     */ {
/*     */   private transient Entry[] table;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private transient int count;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int threshold;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private float loadFactor;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class Entry
/*     */   {
/*     */     int hash;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     int key;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     Object value;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     Entry next;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected Entry(int hash, int key, Object value, Entry next)
/*     */     {
/*  83 */       this.hash = hash;
/*  84 */       this.key = key;
/*  85 */       this.value = value;
/*  86 */       this.next = next;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public IntHashMap()
/*     */   {
/*  95 */     this(20, 0.75F);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IntHashMap(int initialCapacity)
/*     */   {
/* 107 */     this(initialCapacity, 0.75F);
/*     */   }
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
/*     */   public IntHashMap(int initialCapacity, float loadFactor)
/*     */   {
/* 121 */     if (initialCapacity < 0) {
/* 122 */       throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
/*     */     }
/* 124 */     if (loadFactor <= 0.0F) {
/* 125 */       throw new IllegalArgumentException("Illegal Load: " + loadFactor);
/*     */     }
/* 127 */     if (initialCapacity == 0) {
/* 128 */       initialCapacity = 1;
/*     */     }
/*     */     
/* 131 */     this.loadFactor = loadFactor;
/* 132 */     this.table = new Entry[initialCapacity];
/* 133 */     this.threshold = ((int)(initialCapacity * loadFactor));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int size()
/*     */   {
/* 142 */     return this.count;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 152 */     return this.count == 0;
/*     */   }
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
/*     */   public boolean contains(Object value)
/*     */   {
/* 174 */     if (value == null) {
/* 175 */       throw new NullPointerException();
/*     */     }
/*     */     
/* 178 */     Entry[] tab = this.table;
/* 179 */     for (int i = tab.length; i-- > 0;) {
/* 180 */       for (Entry e = tab[i]; e != null; e = e.next) {
/* 181 */         if (e.value.equals(value)) {
/* 182 */           return true;
/*     */         }
/*     */       }
/*     */     }
/* 186 */     return false;
/*     */   }
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
/*     */   public boolean containsValue(Object value)
/*     */   {
/* 201 */     return contains(value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean containsKey(int key)
/*     */   {
/* 214 */     Entry[] tab = this.table;
/* 215 */     int hash = key;
/* 216 */     int index = (hash & 0x7FFFFFFF) % tab.length;
/* 217 */     for (Entry e = tab[index]; e != null; e = e.next) {
/* 218 */       if (e.hash == hash) {
/* 219 */         return true;
/*     */       }
/*     */     }
/* 222 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object get(int key)
/*     */   {
/* 235 */     Entry[] tab = this.table;
/* 236 */     int hash = key;
/* 237 */     int index = (hash & 0x7FFFFFFF) % tab.length;
/* 238 */     for (Entry e = tab[index]; e != null; e = e.next) {
/* 239 */       if (e.hash == hash) {
/* 240 */         return e.value;
/*     */       }
/*     */     }
/* 243 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void rehash()
/*     */   {
/* 256 */     int oldCapacity = this.table.length;
/* 257 */     Entry[] oldMap = this.table;
/*     */     
/* 259 */     int newCapacity = oldCapacity * 2 + 1;
/* 260 */     Entry[] newMap = new Entry[newCapacity];
/*     */     
/* 262 */     this.threshold = ((int)(newCapacity * this.loadFactor));
/* 263 */     this.table = newMap;
/*     */     
/* 265 */     for (int i = oldCapacity; i-- > 0;) {
/* 266 */       for (old = oldMap[i]; old != null;) {
/* 267 */         Entry e = old;
/* 268 */         old = old.next;
/*     */         
/* 270 */         int index = (e.hash & 0x7FFFFFFF) % newCapacity;
/* 271 */         e.next = newMap[index];
/* 272 */         newMap[index] = e;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     Entry old;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object put(int key, Object value)
/*     */   {
/* 294 */     Entry[] tab = this.table;
/* 295 */     int hash = key;
/* 296 */     int index = (hash & 0x7FFFFFFF) % tab.length;
/* 297 */     for (Entry e = tab[index]; e != null; e = e.next) {
/* 298 */       if (e.hash == hash) {
/* 299 */         Object old = e.value;
/* 300 */         e.value = value;
/* 301 */         return old;
/*     */       }
/*     */     }
/*     */     
/* 305 */     if (this.count >= this.threshold)
/*     */     {
/* 307 */       rehash();
/*     */       
/* 309 */       tab = this.table;
/* 310 */       index = (hash & 0x7FFFFFFF) % tab.length;
/*     */     }
/*     */     
/*     */ 
/* 314 */     Entry e = new Entry(hash, key, value, tab[index]);
/* 315 */     tab[index] = e;
/* 316 */     this.count += 1;
/* 317 */     return null;
/*     */   }
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
/*     */   public Object remove(int key)
/*     */   {
/* 332 */     Entry[] tab = this.table;
/* 333 */     int hash = key;
/* 334 */     int index = (hash & 0x7FFFFFFF) % tab.length;
/* 335 */     Entry e = tab[index]; for (Entry prev = null; e != null; e = e.next) {
/* 336 */       if (e.hash == hash) {
/* 337 */         if (prev != null) {
/* 338 */           prev.next = e.next;
/*     */         } else {
/* 340 */           tab[index] = e.next;
/*     */         }
/* 342 */         this.count -= 1;
/* 343 */         Object oldValue = e.value;
/* 344 */         e.value = null;
/* 345 */         return oldValue;
/*     */       }
/* 335 */       prev = e;
/*     */     }
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
/* 348 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void clear()
/*     */   {
/* 355 */     Entry[] tab = this.table;
/* 356 */     int index = tab.length; for (;;) { index--; if (index < 0) break;
/* 357 */       tab[index] = null;
/*     */     }
/* 359 */     this.count = 0;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/apache/commons/lang/IntHashMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */