/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.AbstractCollection;
/*     */ import java.util.AbstractMap;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Collection;
/*     */ import java.util.ConcurrentModificationException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Random;
/*     */ import java.util.Set;
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
/*     */ public class LightHashMap<S, T>
/*     */   extends AbstractMap<S, T>
/*     */   implements Cloneable
/*     */ {
/*  44 */   private static final Object THOMBSTONE = new Object();
/*  45 */   private static final Object NULLKEY = new Object();
/*     */   private static final float DEFAULT_LOAD_FACTOR = 0.75F;
/*     */   private static final int DEFAULT_CAPACITY = 8;
/*     */   
/*     */   public LightHashMap()
/*     */   {
/*  51 */     this(8, 0.75F);
/*     */   }
/*     */   
/*     */   public LightHashMap(int initialCapacity)
/*     */   {
/*  56 */     this(initialCapacity, 0.75F);
/*     */   }
/*     */   
/*     */   public LightHashMap(Map m)
/*     */   {
/*  61 */     this(0);
/*  62 */     if ((m instanceof LightHashMap))
/*     */     {
/*  64 */       LightHashMap lightMap = (LightHashMap)m;
/*  65 */       this.size = lightMap.size;
/*  66 */       this.data = ((Object[])lightMap.data.clone());
/*     */     } else {
/*  68 */       putAll(m);
/*     */     }
/*     */   }
/*     */   
/*     */   public Object clone() {
/*     */     try {
/*  74 */       LightHashMap newMap = (LightHashMap)super.clone();
/*  75 */       newMap.data = ((Object[])this.data.clone());
/*  76 */       return newMap;
/*     */     }
/*     */     catch (CloneNotSupportedException e)
/*     */     {
/*  80 */       e.printStackTrace();
/*  81 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public LightHashMap(int initialCapacity, float loadFactor)
/*     */   {
/*  87 */     if (loadFactor > 1.0F)
/*  88 */       throw new IllegalArgumentException("Load factor must not be > 1");
/*  89 */     this.loadFactor = loadFactor;
/*  90 */     int capacity = 1;
/*  91 */     while (capacity < initialCapacity)
/*  92 */       capacity <<= 1;
/*  93 */     this.data = new Object[capacity * 2];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Set entrySet()
/*     */   {
/* 101 */     return new EntrySet(null);
/*     */   }
/*     */   
/*     */   private abstract class HashIterator implements Iterator {
/* 105 */     protected int nextIdx = -2;
/* 106 */     protected int currentIdx = -2;
/* 107 */     protected final Object[] itData = LightHashMap.this.data;
/*     */     
/*     */     public HashIterator()
/*     */     {
/* 111 */       findNext();
/*     */     }
/*     */     
/*     */     private void findNext() {
/*     */       do {
/* 116 */         this.nextIdx += 2;
/* 117 */       } while ((this.nextIdx < this.itData.length) && ((this.itData[this.nextIdx] == null) || (this.itData[this.nextIdx] == LightHashMap.THOMBSTONE)));
/*     */     }
/*     */     
/*     */     public void remove() {
/* 121 */       if (this.currentIdx == -2)
/* 122 */         throw new IllegalStateException("No entry to delete, use next() first");
/* 123 */       if (this.itData != LightHashMap.this.data)
/* 124 */         throw new ConcurrentModificationException("removal opperation not supported as concurrent structural modification occured");
/* 125 */       LightHashMap.this.removeForIndex(this.currentIdx);
/* 126 */       this.currentIdx = -2;
/*     */     }
/*     */     
/*     */     public boolean hasNext() {
/* 130 */       return this.nextIdx < this.itData.length;
/*     */     }
/*     */     
/*     */     public Object next() {
/* 134 */       if (!hasNext())
/* 135 */         throw new IllegalStateException("No more entries");
/* 136 */       this.currentIdx = this.nextIdx;
/* 137 */       findNext();
/* 138 */       return nextIntern();
/*     */     }
/*     */     
/*     */     abstract Object nextIntern();
/*     */   }
/*     */   
/*     */   private class EntrySet extends AbstractSet { private EntrySet() {}
/*     */     
/* 146 */     public Iterator iterator() { return new EntrySetIterator(null); }
/*     */     
/*     */ 
/*     */ 
/* 150 */     public int size() { return LightHashMap.this.size; }
/*     */     
/*     */     private class EntrySetIterator extends LightHashMap.HashIterator {
/* 153 */       private EntrySetIterator() { super(); }
/*     */       
/* 155 */       public Object nextIntern() { return new Entry(this.currentIdx); }
/*     */       
/*     */       private final class Entry implements Map.Entry
/*     */       {
/*     */         final int entryIndex;
/*     */         
/*     */         public Entry(int idx)
/*     */         {
/* 163 */           this.entryIndex = idx;
/*     */         }
/*     */         
/*     */         public Object getKey() {
/* 167 */           Object key = LightHashMap.EntrySet.EntrySetIterator.this.itData[this.entryIndex];
/* 168 */           return key != LightHashMap.NULLKEY ? key : null;
/*     */         }
/*     */         
/*     */         public Object getValue() {
/* 172 */           return LightHashMap.EntrySet.EntrySetIterator.this.itData[(this.entryIndex + 1)];
/*     */         }
/*     */         
/*     */         public Object setValue(Object value) {
/* 176 */           Object oldValue = LightHashMap.EntrySet.EntrySetIterator.this.itData[(this.entryIndex + 1)];
/* 177 */           LightHashMap.EntrySet.EntrySetIterator.this.itData[(this.entryIndex + 1)] = value;
/* 178 */           return oldValue;
/*     */         }
/*     */         
/*     */         public boolean equals(Object o) {
/* 182 */           if (!(o instanceof Map.Entry))
/* 183 */             return false;
/* 184 */           Map.Entry e = (Map.Entry)o;
/* 185 */           return (getKey() == null ? e.getKey() == null : getKey().equals(e.getKey())) && (getValue() == null ? e.getValue() == null : getValue().equals(e.getValue()));
/*     */         }
/*     */         
/*     */ 
/* 189 */         public int hashCode() { return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() == null ? 0 : getValue().hashCode()); }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private class KeySet extends AbstractSet {
/*     */     private KeySet() {}
/*     */     
/* 197 */     public Iterator iterator() { return new KeySetIterator(null); }
/*     */     
/*     */     private class KeySetIterator extends LightHashMap.HashIterator {
/* 200 */       private KeySetIterator() { super(); }
/*     */       
/* 202 */       Object nextIntern() { Object key = this.itData[this.currentIdx];
/* 203 */         return key != LightHashMap.NULLKEY ? key : null;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 208 */     public int size() { return LightHashMap.this.size; }
/*     */   }
/*     */   
/*     */   private class Values extends AbstractCollection {
/*     */     private Values() {}
/*     */     
/* 214 */     public Iterator iterator() { return new ValueIterator(null); }
/*     */     
/*     */     private class ValueIterator extends LightHashMap.HashIterator {
/* 217 */       private ValueIterator() { super(); }
/*     */       
/* 219 */       Object nextIntern() { return this.itData[(this.currentIdx + 1)]; }
/*     */     }
/*     */     
/*     */     public int size()
/*     */     {
/* 224 */       return LightHashMap.this.size;
/*     */     }
/*     */   }
/*     */   
/*     */   public T put(Object key, Object value) {
/* 229 */     checkCapacity(1);
/* 230 */     return (T)add(key, value, false);
/*     */   }
/*     */   
/*     */   public void putAll(Map m) {
/* 234 */     checkCapacity(m.size());
/* 235 */     for (Iterator it = m.entrySet().iterator(); it.hasNext();)
/*     */     {
/* 237 */       Map.Entry entry = (Map.Entry)it.next();
/* 238 */       add(entry.getKey(), entry.getValue(), true);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Set<S> keySet()
/*     */   {
/* 245 */     return new KeySet(null);
/*     */   }
/*     */   
/*     */   public Collection<T> values() {
/* 249 */     return new Values(null);
/*     */   }
/*     */   
/*     */   public int capacity()
/*     */   {
/* 254 */     return this.data.length >> 1;
/*     */   }
/*     */   
/*     */   public T get(Object key) {
/* 258 */     if (key == null)
/* 259 */       key = NULLKEY;
/* 260 */     return (T)this.data[(nonModifyingFindIndex(key) + 1)];
/*     */   }
/*     */   
/*     */   private Object add(Object key, Object value, boolean bulkAdd) {
/* 264 */     if (key == null)
/* 265 */       key = NULLKEY;
/* 266 */     int idx = bulkAdd ? nonModifyingFindIndex(key) : findIndex(key);
/* 267 */     Object oldValue = this.data[(idx + 1)];
/* 268 */     if ((this.data[idx] == null) || (this.data[idx] == THOMBSTONE))
/*     */     {
/* 270 */       this.data[idx] = key;
/* 271 */       this.size += 1;
/*     */     }
/* 273 */     this.data[(idx + 1)] = value;
/* 274 */     return oldValue;
/*     */   }
/*     */   
/*     */   public T remove(Object key) {
/* 278 */     if (this.size == 0)
/* 279 */       return null;
/* 280 */     if (key == null)
/* 281 */       key = NULLKEY;
/* 282 */     int idx = findIndex(key);
/* 283 */     if (keysEqual(this.data[idx], key))
/* 284 */       return (T)removeForIndex(idx);
/* 285 */     return null;
/*     */   }
/*     */   
/*     */   private Object removeForIndex(int idx)
/*     */   {
/* 290 */     Object oldValue = this.data[(idx + 1)];
/* 291 */     this.data[idx] = THOMBSTONE;
/* 292 */     this.data[(idx + 1)] = null;
/* 293 */     this.size -= 1;
/* 294 */     return oldValue;
/*     */   }
/*     */   
/*     */   public void clear() {
/* 298 */     this.size = 0;
/* 299 */     int capacity = 1;
/* 300 */     while (capacity < 8)
/* 301 */       capacity <<= 1;
/* 302 */     this.data = new Object[capacity * 2];
/*     */   }
/*     */   
/*     */   public boolean containsKey(Object key) {
/* 306 */     if (this.size == 0)
/* 307 */       return false;
/* 308 */     if (key == null)
/* 309 */       key = NULLKEY;
/* 310 */     return keysEqual(key, this.data[nonModifyingFindIndex(key)]);
/*     */   }
/*     */   
/*     */   public boolean containsValue(Object value) {
/* 314 */     if (value != null)
/*     */     {
/* 316 */       for (int i = 0; i < this.data.length; i += 2)
/* 317 */         if (value.equals(this.data[(i + 1)]))
/* 318 */           return true;
/*     */     } else
/* 320 */       for (int i = 0; i < this.data.length; i += 2)
/* 321 */         if ((this.data[(i + 1)] == null) && (this.data[i] != null) && (this.data[i] != THOMBSTONE))
/* 322 */           return true;
/* 323 */     return false;
/*     */   }
/*     */   
/*     */   private final boolean keysEqual(Object o1, Object o2) {
/* 327 */     return (o1 == o2) || ((o1 != null) && (o2 != null) && (o1.hashCode() == o2.hashCode()) && (o1.equals(o2)));
/*     */   }
/*     */   
/*     */   private int findIndex(Object keyToFind) {
/* 331 */     int hash = keyToFind.hashCode() << 1;
/*     */     
/*     */ 
/*     */ 
/* 335 */     int probe = 1;
/* 336 */     int newIndex = hash & this.data.length - 1;
/* 337 */     int thombStoneIndex = -1;
/* 338 */     int thombStoneCount = 0;
/* 339 */     int thombStoneThreshold = Math.min((this.data.length >> 1) - this.size, 100);
/*     */     
/* 341 */     while ((this.data[newIndex] != null) && (!keysEqual(this.data[newIndex], keyToFind)))
/*     */     {
/* 343 */       if (this.data[newIndex] == THOMBSTONE)
/*     */       {
/* 345 */         if (thombStoneIndex == -1)
/* 346 */           thombStoneIndex = newIndex;
/* 347 */         thombStoneCount++;
/* 348 */         if (thombStoneCount * 2 > thombStoneThreshold)
/*     */         {
/* 350 */           compactify(0.0F);
/* 351 */           thombStoneIndex = -1;
/* 352 */           probe = 0;
/* 353 */           thombStoneCount = 0;
/*     */         }
/*     */       }
/*     */       
/* 357 */       newIndex = hash + probe + probe * probe & this.data.length - 1;
/* 358 */       probe++;
/*     */     }
/*     */     
/* 361 */     if ((thombStoneIndex != -1) && (!keysEqual(this.data[newIndex], keyToFind)))
/* 362 */       return thombStoneIndex;
/* 363 */     return newIndex;
/*     */   }
/*     */   
/*     */   private int nonModifyingFindIndex(Object keyToFind) {
/* 367 */     int hash = keyToFind.hashCode() << 1;
/*     */     
/*     */ 
/*     */ 
/* 371 */     int probe = 1;
/* 372 */     int newIndex = hash & this.data.length - 1;
/* 373 */     int thombStoneIndex = -1;
/*     */     
/* 375 */     while ((this.data[newIndex] != null) && (!keysEqual(this.data[newIndex], keyToFind)) && (probe < this.data.length >> 1))
/*     */     {
/* 377 */       if ((this.data[newIndex] == THOMBSTONE) && (thombStoneIndex == -1))
/* 378 */         thombStoneIndex = newIndex;
/* 379 */       newIndex = hash + probe + probe * probe & this.data.length - 1;
/* 380 */       probe++;
/*     */     }
/* 382 */     if ((thombStoneIndex != -1) && (!keysEqual(this.data[newIndex], keyToFind)))
/* 383 */       return thombStoneIndex;
/* 384 */     return newIndex;
/*     */   }
/*     */   
/*     */   private void checkCapacity(int n)
/*     */   {
/* 389 */     int currentCapacity = this.data.length >> 1;
/* 390 */     if (this.size + n < currentCapacity * this.loadFactor)
/* 391 */       return;
/* 392 */     int newCapacity = currentCapacity;
/*     */     do {
/* 394 */       newCapacity <<= 1;
/* 395 */     } while (newCapacity * this.loadFactor < this.size + n);
/* 396 */     adjustCapacity(newCapacity);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   final float loadFactor;
/*     */   
/*     */ 
/*     */   int size;
/*     */   
/*     */   Object[] data;
/*     */   
/*     */   public void compactify(float compactingLoadFactor)
/*     */   {
/* 410 */     int newCapacity = 1;
/* 411 */     float adjustedLoadFactor = Math.abs(compactingLoadFactor);
/* 412 */     if ((adjustedLoadFactor <= 0.0F) || (adjustedLoadFactor >= 1.0F))
/* 413 */       adjustedLoadFactor = this.loadFactor;
/* 414 */     while (newCapacity * adjustedLoadFactor < this.size + 1)
/* 415 */       newCapacity <<= 1;
/* 416 */     if ((newCapacity < this.data.length / 2) || (compactingLoadFactor >= 0.0F))
/* 417 */       adjustCapacity(newCapacity);
/*     */   }
/*     */   
/*     */   private void adjustCapacity(int newSize) {
/* 421 */     Object[] oldData = this.data;
/* 422 */     this.data = new Object[newSize * 2];
/* 423 */     this.size = 0;
/* 424 */     for (int i = 0; i < oldData.length; i += 2)
/*     */     {
/* 426 */       if ((oldData[i] != null) && (oldData[i] != THOMBSTONE))
/*     */       {
/* 428 */         add(oldData[i], oldData[(i + 1)], true); }
/*     */     }
/*     */   }
/*     */   
/*     */   static void test() {
/* 433 */     Random rnd = new Random();
/* 434 */     byte[] buffer = new byte[5];
/* 435 */     String[] fillData = new String[1950351];
/* 436 */     for (int i = 0; i < fillData.length; i++)
/*     */     {
/* 438 */       rnd.nextBytes(buffer);
/* 439 */       fillData[i] = new String(buffer);
/* 440 */       fillData[i].hashCode();
/*     */     }
/*     */     
/* 443 */     Map m1 = new HashMap();
/* 444 */     Map m2 = new LightHashMap();
/* 445 */     System.out.println("fill:");
/* 446 */     long time = System.currentTimeMillis();
/* 447 */     for (int i = 0; i < fillData.length; i++)
/* 448 */       m1.put(fillData[i], buffer);
/* 449 */     System.out.println(System.currentTimeMillis() - time);
/* 450 */     time = System.currentTimeMillis();
/* 451 */     for (int i = 0; i < fillData.length; i++)
/* 452 */       m2.put(fillData[i], buffer);
/* 453 */     System.out.println(System.currentTimeMillis() - time);
/* 454 */     System.out.println("replace-fill:");
/* 455 */     time = System.currentTimeMillis();
/* 456 */     for (int i = 0; i < fillData.length; i++)
/* 457 */       m1.put(fillData[i], buffer);
/* 458 */     System.out.println(System.currentTimeMillis() - time);
/* 459 */     time = System.currentTimeMillis();
/* 460 */     for (int i = 0; i < fillData.length; i++)
/* 461 */       m2.put(fillData[i], buffer);
/* 462 */     System.out.println(System.currentTimeMillis() - time);
/* 463 */     System.out.println("get:");
/* 464 */     time = System.currentTimeMillis();
/* 465 */     for (int i = 0; i < fillData.length; i++)
/* 466 */       m1.get(fillData[i]);
/* 467 */     System.out.println(System.currentTimeMillis() - time);
/* 468 */     time = System.currentTimeMillis();
/* 469 */     for (int i = 0; i < fillData.length; i++)
/* 470 */       m2.get(fillData[i]);
/* 471 */     System.out.println(System.currentTimeMillis() - time);
/* 472 */     System.out.println("compactify light map");
/* 473 */     time = System.currentTimeMillis();
/* 474 */     ((LightHashMap)m2).compactify(0.9F);
/* 475 */     System.out.println(System.currentTimeMillis() - time);
/* 476 */     System.out.println("transfer to hashmap");
/* 477 */     time = System.currentTimeMillis();
/* 478 */     new HashMap(m1);
/* 479 */     System.out.println(System.currentTimeMillis() - time);
/* 480 */     time = System.currentTimeMillis();
/* 481 */     new HashMap(m2);
/* 482 */     System.out.println(System.currentTimeMillis() - time);
/* 483 */     System.out.println("transfer to lighthashmap");
/* 484 */     time = System.currentTimeMillis();
/* 485 */     new LightHashMap(m1);
/* 486 */     System.out.println(System.currentTimeMillis() - time);
/* 487 */     time = System.currentTimeMillis();
/* 488 */     new LightHashMap(m2);
/* 489 */     System.out.println(System.currentTimeMillis() - time);
/* 490 */     System.out.println("remove entry by entry");
/* 491 */     time = System.currentTimeMillis();
/* 492 */     for (int i = 0; i < fillData.length; i++)
/* 493 */       m1.remove(fillData[i]);
/* 494 */     System.out.println(System.currentTimeMillis() - time);
/* 495 */     time = System.currentTimeMillis();
/* 496 */     for (int i = 0; i < fillData.length; i++)
/* 497 */       m2.remove(fillData[i]);
/* 498 */     System.out.println(System.currentTimeMillis() - time);
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 502 */     System.out.println("Call with -Xmx300m -Xcomp -server");
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
/*     */     try
/*     */     {
/* 530 */       Thread.sleep(5000L);
/*     */     }
/*     */     catch (InterruptedException e)
/*     */     {
/* 534 */       e.printStackTrace();
/*     */     }
/* 536 */     test();
/* 537 */     System.out.println("-------------------------------------");
/* 538 */     System.gc();
/*     */     try
/*     */     {
/* 541 */       Thread.sleep(300L);
/*     */     }
/*     */     catch (InterruptedException e)
/*     */     {
/* 545 */       e.printStackTrace();
/*     */     }
/* 547 */     test();
/*     */     
/* 549 */     System.out.println("\n\nPerforming sanity tests");
/* 550 */     Random rnd = new Random();
/* 551 */     byte[] buffer = new byte[25];
/* 552 */     String[] fillData = new String['И'];
/* 553 */     for (int i = 0; i < fillData.length; i++)
/*     */     {
/* 555 */       rnd.nextBytes(buffer);
/* 556 */       fillData[i] = new String(buffer);
/* 557 */       fillData[i].hashCode();
/*     */     }
/*     */     
/* 560 */     Map m1 = new HashMap();
/* 561 */     Map m2 = new LightHashMap();
/*     */     
/* 563 */     for (int i = 0; i < fillData.length * 10; i++)
/*     */     {
/* 565 */       int random = rnd.nextInt(fillData.length);
/*     */       
/* 567 */       m1.put(null, fillData[(i % fillData.length)]);
/* 568 */       m2.put(null, fillData[(i % fillData.length)]);
/* 569 */       if (!m1.equals(m2))
/* 570 */         System.out.println("Error 0");
/* 571 */       m1.put(fillData[random], fillData[(i % fillData.length)]);
/* 572 */       m2.put(fillData[random], fillData[(i % fillData.length)]);
/* 573 */       if (!m1.equals(m2)) {
/* 574 */         System.out.println("Error 1");
/*     */       }
/*     */     }
/*     */     
/* 578 */     for (int i = 0; i < fillData.length / 2; i++)
/*     */     {
/* 580 */       int random = rnd.nextInt(fillData.length);
/* 581 */       m1.remove(fillData[random]);
/* 582 */       m2.remove(fillData[random]);
/* 583 */       if (!m1.equals(m2)) {
/* 584 */         System.out.println("Error 2");
/*     */       }
/*     */     }
/*     */     
/* 588 */     for (int i = 0; i < fillData.length * 10; i++)
/*     */     {
/* 590 */       int random = rnd.nextInt(fillData.length);
/* 591 */       m1.put(fillData[random], fillData[(i % fillData.length)]);
/* 592 */       m1.put(null, fillData[(i % fillData.length)]);
/* 593 */       m2.put(fillData[random], fillData[(i % fillData.length)]);
/* 594 */       m2.put(null, fillData[(i % fillData.length)]);
/* 595 */       if (!m1.equals(m2)) {
/* 596 */         System.out.println("Error 3");
/*     */       }
/*     */     }
/* 599 */     Iterator i1 = m1.entrySet().iterator();
/* 600 */     Iterator i2 = m2.entrySet().iterator();
/*     */     
/* 602 */     while (i1.hasNext())
/*     */     {
/* 604 */       i1.next();
/* 605 */       i1.remove();
/* 606 */       i2.next();
/* 607 */       i2.remove();
/*     */     }
/*     */     
/* 610 */     if (!m1.equals(m2)) {
/* 611 */       System.out.println("Error 4");
/*     */     }
/*     */     
/*     */ 
/* 615 */     m2.clear();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 624 */     for (int i = 0; i < 100000; i++)
/*     */     {
/* 626 */       rnd.nextBytes(buffer);
/* 627 */       String s = new String(buffer);
/* 628 */       m2.put(s, buffer);
/* 629 */       m2.containsKey(s);
/* 630 */       m2.remove(s);
/*     */     }
/*     */     
/* 633 */     System.out.println("checks done");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/LightHashMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */