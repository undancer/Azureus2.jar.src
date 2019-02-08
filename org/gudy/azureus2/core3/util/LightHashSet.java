/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.ConcurrentModificationException;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
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
/*     */ 
/*     */ 
/*     */ public class LightHashSet
/*     */   extends AbstractSet
/*     */   implements Cloneable
/*     */ {
/*  43 */   private static final Object THOMBSTONE = new Object();
/*  44 */   private static final Object NULLKEY = new Object();
/*     */   private static final float DEFAULT_LOAD_FACTOR = 0.75F;
/*     */   private static final int DEFAULT_CAPACITY = 8;
/*     */   
/*     */   public LightHashSet()
/*     */   {
/*  50 */     this(8, 0.75F);
/*     */   }
/*     */   
/*     */   public LightHashSet(int initialCapacity)
/*     */   {
/*  55 */     this(initialCapacity, 0.75F);
/*     */   }
/*     */   
/*     */   public LightHashSet(Collection c)
/*     */   {
/*  60 */     this(0);
/*  61 */     if ((c instanceof LightHashSet))
/*     */     {
/*  63 */       LightHashSet lightMap = (LightHashSet)c;
/*  64 */       this.size = lightMap.size;
/*  65 */       this.data = ((Object[])lightMap.data.clone());
/*     */     } else {
/*  67 */       addAll(c);
/*     */     }
/*     */   }
/*     */   
/*     */   public Object clone() {
/*     */     try {
/*  73 */       LightHashMap newMap = (LightHashMap)super.clone();
/*  74 */       newMap.data = ((Object[])this.data.clone());
/*  75 */       return newMap;
/*     */     }
/*     */     catch (CloneNotSupportedException e)
/*     */     {
/*  79 */       e.printStackTrace();
/*  80 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public LightHashSet(int initialCapacity, float loadFactor)
/*     */   {
/*  86 */     if (loadFactor > 1.0F)
/*  87 */       throw new IllegalArgumentException("Load factor must not be > 1");
/*  88 */     this.loadFactor = loadFactor;
/*  89 */     int capacity = 1;
/*  90 */     while (capacity < initialCapacity)
/*  91 */       capacity <<= 1;
/*  92 */     this.data = new Object[capacity];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Iterator iterator()
/*     */   {
/* 101 */     return new HashIterator();
/*     */   }
/*     */   
/*     */   private class HashIterator implements Iterator {
/* 105 */     private int nextIdx = -1;
/* 106 */     private int currentIdx = -1;
/* 107 */     private final Object[] itData = LightHashSet.this.data;
/*     */     
/*     */     public HashIterator()
/*     */     {
/* 111 */       findNext();
/*     */     }
/*     */     
/*     */     private void findNext() {
/*     */       do {
/* 116 */         this.nextIdx += 1;
/* 117 */       } while ((this.nextIdx < this.itData.length) && ((this.itData[this.nextIdx] == null) || (this.itData[this.nextIdx] == LightHashSet.THOMBSTONE)));
/*     */     }
/*     */     
/*     */     public void remove() {
/* 121 */       if (this.currentIdx == -1)
/* 122 */         throw new IllegalStateException("No entry to delete, use next() first");
/* 123 */       if (this.itData != LightHashSet.this.data)
/* 124 */         throw new ConcurrentModificationException("removal opperation not supported as concurrent structural modification occured");
/* 125 */       LightHashSet.this.removeForIndex(this.currentIdx);
/* 126 */       this.currentIdx = -1;
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
/* 138 */       Object key = this.itData[this.currentIdx];
/* 139 */       return key != LightHashSet.NULLKEY ? key : null;
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean add(Object key)
/*     */   {
/* 145 */     checkCapacity(1);
/* 146 */     return addInternal(key, false);
/*     */   }
/*     */   
/*     */   public int size() {
/* 150 */     return this.size;
/*     */   }
/*     */   
/*     */   public boolean addAll(Collection c) {
/* 154 */     checkCapacity(c.size());
/* 155 */     boolean changed = false;
/* 156 */     for (Iterator it = c.iterator(); it.hasNext();)
/*     */     {
/* 158 */       changed |= addInternal(it.next(), true);
/*     */     }
/*     */     
/*     */ 
/* 162 */     return changed;
/*     */   }
/*     */   
/*     */   public int capacity()
/*     */   {
/* 167 */     return this.data.length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object get(Object key)
/*     */   {
/* 176 */     if (key == null)
/* 177 */       key = NULLKEY;
/* 178 */     int idx = nonModifyingFindIndex(key);
/* 179 */     if (keysEqual(this.data[idx], key))
/* 180 */       return this.data[idx];
/* 181 */     return null;
/*     */   }
/*     */   
/*     */   private boolean addInternal(Object key, boolean bulkAdd) {
/* 185 */     if (key == null)
/* 186 */       key = NULLKEY;
/* 187 */     int idx = bulkAdd ? nonModifyingFindIndex(key) : findIndex(key);
/* 188 */     if ((this.data[idx] == null) || (this.data[idx] == THOMBSTONE))
/*     */     {
/* 190 */       this.data[idx] = key;
/* 191 */       this.size += 1;
/* 192 */       return true;
/*     */     }
/* 194 */     return false;
/*     */   }
/*     */   
/*     */   public boolean remove(Object key) {
/* 198 */     if (this.size == 0)
/* 199 */       return false;
/* 200 */     if (key == null)
/* 201 */       key = NULLKEY;
/* 202 */     int idx = findIndex(key);
/* 203 */     if (keysEqual(key, this.data[idx]))
/*     */     {
/* 205 */       removeForIndex(idx);
/* 206 */       return true;
/*     */     }
/* 208 */     return false;
/*     */   }
/*     */   
/*     */   private void removeForIndex(int idx)
/*     */   {
/* 213 */     this.data[idx] = THOMBSTONE;
/* 214 */     this.size -= 1;
/*     */   }
/*     */   
/*     */   public void clear() {
/* 218 */     this.size = 0;
/* 219 */     int capacity = 1;
/* 220 */     while (capacity < 8)
/* 221 */       capacity <<= 1;
/* 222 */     this.data = new Object[capacity];
/*     */   }
/*     */   
/*     */   public boolean contains(Object key) {
/* 226 */     if (this.size == 0)
/* 227 */       return false;
/* 228 */     if (key == null)
/* 229 */       key = NULLKEY;
/* 230 */     return keysEqual(key, this.data[nonModifyingFindIndex(key)]);
/*     */   }
/*     */   
/*     */   private final boolean keysEqual(Object o1, Object o2) {
/* 234 */     return (o1 == o2) || ((o1 != null) && (o2 != null) && (o1.hashCode() == o2.hashCode()) && (o1.equals(o2)));
/*     */   }
/*     */   
/*     */   private int findIndex(Object keyToFind) {
/* 238 */     int hash = keyToFind.hashCode();
/*     */     
/*     */ 
/*     */ 
/* 242 */     int probe = 1;
/* 243 */     int newIndex = hash & this.data.length - 1;
/* 244 */     int thombStoneIndex = -1;
/* 245 */     int thombStoneCount = 0;
/* 246 */     int thombStoneThreshold = Math.min(this.data.length - this.size, 100);
/*     */     
/* 248 */     while ((this.data[newIndex] != null) && (!keysEqual(this.data[newIndex], keyToFind)))
/*     */     {
/* 250 */       if (this.data[newIndex] == THOMBSTONE)
/*     */       {
/* 252 */         if (thombStoneIndex == -1)
/* 253 */           thombStoneIndex = newIndex;
/* 254 */         thombStoneCount++;
/* 255 */         if (thombStoneCount * 2 > thombStoneThreshold)
/*     */         {
/* 257 */           compactify(0.0F);
/* 258 */           thombStoneIndex = -1;
/* 259 */           probe = 0;
/* 260 */           thombStoneCount = 0;
/*     */         }
/*     */       }
/*     */       
/* 264 */       newIndex = hash + (probe + probe * probe >> 1) & this.data.length - 1;
/* 265 */       probe++;
/*     */     }
/*     */     
/* 268 */     if ((thombStoneIndex != -1) && (!keysEqual(this.data[newIndex], keyToFind)))
/* 269 */       return thombStoneIndex;
/* 270 */     return newIndex;
/*     */   }
/*     */   
/*     */   private int nonModifyingFindIndex(Object keyToFind) {
/* 274 */     int hash = keyToFind.hashCode();
/*     */     
/*     */ 
/*     */ 
/* 278 */     int probe = 1;
/* 279 */     int newIndex = hash & this.data.length - 1;
/* 280 */     int thombStoneIndex = -1;
/*     */     
/* 282 */     while ((this.data[newIndex] != null) && (!keysEqual(this.data[newIndex], keyToFind)) && (probe < this.data.length))
/*     */     {
/* 284 */       if ((this.data[newIndex] == THOMBSTONE) && (thombStoneIndex == -1))
/* 285 */         thombStoneIndex = newIndex;
/* 286 */       newIndex = hash + (probe + probe * probe >> 1) & this.data.length - 1;
/* 287 */       probe++;
/*     */     }
/* 289 */     if ((thombStoneIndex != -1) && (!keysEqual(this.data[newIndex], keyToFind)))
/* 290 */       return thombStoneIndex;
/* 291 */     return newIndex;
/*     */   }
/*     */   
/*     */   private void checkCapacity(int n)
/*     */   {
/* 296 */     int currentCapacity = this.data.length;
/* 297 */     if (this.size + n < currentCapacity * this.loadFactor)
/* 298 */       return;
/* 299 */     int newCapacity = currentCapacity;
/*     */     do {
/* 301 */       newCapacity <<= 1;
/* 302 */     } while (newCapacity * this.loadFactor < this.size + n);
/* 303 */     adjustCapacity(newCapacity);
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
/* 317 */     int newCapacity = 1;
/* 318 */     float adjustedLoadFactor = Math.abs(compactingLoadFactor);
/* 319 */     if ((adjustedLoadFactor <= 0.0F) || (adjustedLoadFactor >= 1.0F))
/* 320 */       adjustedLoadFactor = this.loadFactor;
/* 321 */     while (newCapacity * adjustedLoadFactor < this.size + 1)
/* 322 */       newCapacity <<= 1;
/* 323 */     if ((newCapacity < this.data.length) || (compactingLoadFactor >= 0.0F))
/* 324 */       adjustCapacity(newCapacity);
/*     */   }
/*     */   
/*     */   private void adjustCapacity(int newSize) {
/* 328 */     Object[] oldData = this.data;
/* 329 */     this.data = new Object[newSize];
/* 330 */     this.size = 0;
/* 331 */     for (int i = 0; i < oldData.length; i++)
/*     */     {
/* 333 */       if ((oldData[i] != null) && (oldData[i] != THOMBSTONE))
/*     */       {
/* 335 */         addInternal(oldData[i], true); }
/*     */     }
/*     */   }
/*     */   
/*     */   static void test() {
/* 340 */     Random rnd = new Random();
/* 341 */     byte[] buffer = new byte[5];
/* 342 */     String[] fillData = new String[975175];
/* 343 */     for (int i = 0; i < fillData.length; i++)
/*     */     {
/* 345 */       rnd.nextBytes(buffer);
/* 346 */       fillData[i] = new String(buffer);
/* 347 */       fillData[i].hashCode();
/*     */     }
/*     */     
/* 350 */     Set s1 = new HashSet();
/* 351 */     Set s2 = new LightHashSet();
/* 352 */     System.out.println("fill:");
/* 353 */     long time = System.currentTimeMillis();
/* 354 */     Collections.addAll(s1, fillData);
/* 355 */     System.out.println(System.currentTimeMillis() - time);
/* 356 */     time = System.currentTimeMillis();
/* 357 */     Collections.addAll(s2, fillData);
/* 358 */     System.out.println(System.currentTimeMillis() - time);
/* 359 */     System.out.println("replace-fill:");
/* 360 */     time = System.currentTimeMillis();
/* 361 */     Collections.addAll(s1, fillData);
/* 362 */     System.out.println(System.currentTimeMillis() - time);
/* 363 */     time = System.currentTimeMillis();
/* 364 */     Collections.addAll(s2, fillData);
/* 365 */     System.out.println(System.currentTimeMillis() - time);
/* 366 */     System.out.println("get:");
/* 367 */     time = System.currentTimeMillis();
/* 368 */     for (int i = 0; i < fillData.length; i++)
/* 369 */       s1.contains(fillData[i]);
/* 370 */     System.out.println(System.currentTimeMillis() - time);
/* 371 */     time = System.currentTimeMillis();
/* 372 */     for (int i = 0; i < fillData.length; i++)
/* 373 */       s2.contains(fillData[i]);
/* 374 */     System.out.println(System.currentTimeMillis() - time);
/* 375 */     System.out.println("compactify light map");
/* 376 */     time = System.currentTimeMillis();
/* 377 */     ((LightHashSet)s2).compactify(0.95F);
/* 378 */     System.out.println(System.currentTimeMillis() - time);
/* 379 */     System.out.println("transfer to hashmap");
/* 380 */     time = System.currentTimeMillis();
/* 381 */     new HashSet(s1);
/* 382 */     System.out.println(System.currentTimeMillis() - time);
/* 383 */     time = System.currentTimeMillis();
/* 384 */     new HashSet(s2);
/* 385 */     System.out.println(System.currentTimeMillis() - time);
/* 386 */     System.out.println("transfer to lighthashmap");
/* 387 */     time = System.currentTimeMillis();
/* 388 */     new LightHashSet(s1);
/* 389 */     System.out.println(System.currentTimeMillis() - time);
/* 390 */     time = System.currentTimeMillis();
/* 391 */     new LightHashSet(s2);
/* 392 */     System.out.println(System.currentTimeMillis() - time);
/* 393 */     System.out.println("remove entry by entry");
/* 394 */     time = System.currentTimeMillis();
/* 395 */     for (int i = 0; i < fillData.length; i++)
/* 396 */       s1.remove(fillData[i]);
/* 397 */     System.out.println(System.currentTimeMillis() - time);
/* 398 */     time = System.currentTimeMillis();
/* 399 */     for (int i = 0; i < fillData.length; i++)
/* 400 */       s2.remove(fillData[i]);
/* 401 */     System.out.println(System.currentTimeMillis() - time);
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 405 */     System.out.println("Call with -Xmx300m -Xcomp -server");
/* 406 */     Thread.currentThread().setPriority(10);
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
/* 433 */       Thread.sleep(300L);
/*     */     }
/*     */     catch (InterruptedException e)
/*     */     {
/* 437 */       e.printStackTrace();
/*     */     }
/* 439 */     test();
/* 440 */     System.out.println("-------------------------------------");
/* 441 */     System.gc();
/*     */     try
/*     */     {
/* 444 */       Thread.sleep(300L);
/*     */     }
/*     */     catch (InterruptedException e)
/*     */     {
/* 448 */       e.printStackTrace();
/*     */     }
/* 450 */     test();
/*     */     
/* 452 */     System.out.println("\n\nPerforming sanity tests");
/* 453 */     Random rnd = new Random();
/* 454 */     byte[] buffer = new byte[25];
/* 455 */     String[] fillData = new String['И'];
/* 456 */     for (int i = 0; i < fillData.length; i++)
/*     */     {
/* 458 */       rnd.nextBytes(buffer);
/* 459 */       fillData[i] = new String(buffer);
/* 460 */       fillData[i].hashCode();
/*     */     }
/*     */     
/* 463 */     Set s1 = new HashSet();
/* 464 */     Set s2 = new LightHashSet();
/*     */     
/* 466 */     for (int i = 0; i < fillData.length * 10; i++)
/*     */     {
/* 468 */       int random = rnd.nextInt(fillData.length);
/*     */       
/* 470 */       s1.add(null);
/* 471 */       s2.add(null);
/* 472 */       if (!s1.equals(s2))
/* 473 */         System.out.println("Error 0");
/* 474 */       s1.add(fillData[random]);
/* 475 */       s2.add(fillData[random]);
/* 476 */       if (!s1.equals(s2)) {
/* 477 */         System.out.println("Error 1");
/*     */       }
/*     */     }
/*     */     
/* 481 */     for (int i = 0; i < fillData.length / 2; i++)
/*     */     {
/* 483 */       int random = rnd.nextInt(fillData.length);
/* 484 */       s1.remove(fillData[random]);
/* 485 */       s2.remove(fillData[random]);
/* 486 */       if (!s1.equals(s2)) {
/* 487 */         System.out.println("Error 2");
/*     */       }
/*     */     }
/*     */     
/* 491 */     for (int i = 0; i < fillData.length * 10; i++)
/*     */     {
/* 493 */       int random = rnd.nextInt(fillData.length);
/* 494 */       s1.add(fillData[random]);
/* 495 */       s1.add(null);
/* 496 */       s2.add(fillData[random]);
/* 497 */       s2.add(null);
/* 498 */       if (!s1.equals(s2)) {
/* 499 */         System.out.println("Error 3");
/*     */       }
/*     */     }
/* 502 */     Iterator i1 = s1.iterator();
/* 503 */     Iterator i2 = s2.iterator();
/*     */     
/* 505 */     while (i1.hasNext())
/*     */     {
/* 507 */       i1.next();
/* 508 */       i1.remove();
/* 509 */       i2.next();
/* 510 */       i2.remove();
/*     */     }
/*     */     
/* 513 */     if (!s1.equals(s2)) {
/* 514 */       System.out.println("Error 4");
/*     */     }
/*     */     
/*     */ 
/* 518 */     s2.clear();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 528 */     for (int i = 0; i < 100000; i++)
/*     */     {
/* 530 */       rnd.nextBytes(buffer);
/* 531 */       String s = new String(buffer);
/* 532 */       s2.add(s);
/* 533 */       s2.contains(s);
/* 534 */       s2.remove(s);
/*     */     }
/*     */     
/* 537 */     System.out.println("checks done");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/LightHashSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */