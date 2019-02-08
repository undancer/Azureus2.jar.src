/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.HashCodeUtils;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReadWriteLock;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock;
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
/*     */ public class StringInterner
/*     */ {
/*  43 */   public static boolean DISABLE_INTERNING = false;
/*     */   
/*     */   private static final int SCHEDULED_CLEANUP_INTERVAL = 60000;
/*     */   
/*     */   private static final boolean TRACE_CLEANUP = false;
/*     */   
/*     */   private static final boolean TRACE_MULTIHITS = false;
/*     */   
/*     */   private static final int IMMEDIATE_CLEANUP_TRIGGER = 2000;
/*     */   
/*     */   private static final int IMMEDIATE_CLEANUP_GOAL = 1500;
/*     */   private static final int SCHEDULED_CLEANUP_TRIGGER = 1500;
/*     */   private static final int SCHEDULED_CLEANUP_GOAL = 1000;
/*     */   private static final int SCHEDULED_AGING_THRESHOLD = 750;
/*  57 */   private static final LightHashSet managedInterningSet = new LightHashSet(800);
/*  58 */   private static final LightHashSet unmanagedInterningSet = new LightHashSet();
/*  59 */   static final ReadWriteLock managedSetLock = new ReentrantReadWriteLock();
/*     */   
/*  61 */   private static final ReferenceQueue managedRefQueue = new ReferenceQueue();
/*  62 */   private static final ReferenceQueue unmanagedRefQueue = new ReferenceQueue();
/*     */   
/*  64 */   private static final String[] COMMON_KEYS = { "src", "port", "prot", "ip", "udpport", "azver", "httpport", "downloaded", "Content", "Refresh On", "path.utf-8", "uploaded", "completed", "persistent", "attributes", "encoding", "azureus_properties", "stats.download.added.time", "networks", "p1", "resume data", "dndflags", "blocks", "resume", "primaryfile", "resumecomplete", "data", "peersources", "name.utf-8", "valid", "torrent filename", "parameters", "secrets", "timesincedl", "tracker_cache", "filedownloaded", "timesinceul", "tracker_peers", "trackerclientextensions", "GlobalRating", "comment.utf-8", "Count", "String", "stats.counted", "Thumbnail", "Plugin.<internal>.DDBaseTTTorrent::sha1", "type", "Title", "displayname", "Publisher", "Creation Date", "Revision Date", "Content Hash", "flags", "stats.download.completed.time", "Description", "Progressive", "Content Type", "QOS Class", "DRM", "hash", "ver", "id", "body", "seed", "eip", "rid", "iip", "dp2", "tp", "orig", "dp", "Quality", "private", "dht_backup_enable", "max.uploads", "filelinks", "Speed Bps", "cdn_properties", "sha1", "ed2k", "DRM Key", "Plugin.aeseedingengine.attributes", "initial_seed", "dht_backup_requested", "ta", "size", "DIRECTOR PUBLISH", "Plugin.azdirector.ContentMap", "dateadded", "bytesin", "announces", "status", "bytesout", "scrapes", "passive" };
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
/*  80 */   private static final ByteArrayHashMap byte_map = new ByteArrayHashMap(COMMON_KEYS.length);
/*     */   
/*     */   static {
/*     */     try {
/*  84 */       for (int i = 0; i < COMMON_KEYS.length; i++)
/*     */       {
/*  86 */         byte_map.put(COMMON_KEYS[i].getBytes("ISO-8859-1"), COMMON_KEYS[i]);
/*  87 */         managedInterningSet.add(new WeakStringEntry(COMMON_KEYS[i]));
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/*  91 */       e.printStackTrace();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  97 */     new AEThread2("asyncify", true)
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 102 */         SimpleTimer.addPeriodicEvent("StringInterner:cleaner", 60000L, new TimerEventPerformer() {
/*     */           public void perform(TimerEvent event) {
/* 104 */             StringInterner.managedSetLock.writeLock().lock();
/*     */             try {
/* 106 */               StringInterner.sanitize(true);
/*     */             } finally {
/* 108 */               StringInterner.managedSetLock.writeLock().unlock();
/*     */             }
/*     */             
/*     */ 
/* 112 */             StringInterner.access$100();
/*     */           }
/*     */         });
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String intern(byte[] bytes)
/*     */   {
/* 126 */     String res = (String)byte_map.get(bytes);
/*     */     
/*     */ 
/*     */ 
/* 130 */     return res;
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
/*     */   public static Object internObject(Object toIntern)
/*     */   {
/* 144 */     if (DISABLE_INTERNING) {
/* 145 */       return toIntern;
/*     */     }
/*     */     
/* 148 */     if (toIntern == null) {
/* 149 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 153 */     WeakEntry checkEntry = new WeakEntry(toIntern, unmanagedRefQueue);
/*     */     Object internedItem;
/* 155 */     synchronized (unmanagedInterningSet)
/*     */     {
/* 157 */       WeakEntry internedEntry = (WeakEntry)unmanagedInterningSet.get(checkEntry);
/*     */       Object internedItem;
/* 159 */       if ((internedEntry == null) || ((internedItem = internedEntry.get()) == null))
/*     */       {
/* 161 */         internedItem = toIntern;
/* 162 */         if (!unmanagedInterningSet.add(checkEntry)) {
/* 163 */           System.out.println("unexpected modification");
/*     */         }
/*     */       }
/* 166 */       sanitizeLight();
/*     */     }
/*     */     
/*     */ 
/* 170 */     if (!toIntern.equals(internedItem)) {
/* 171 */       System.err.println("mismatch");
/*     */     }
/* 173 */     return internedItem;
/*     */   }
/*     */   
/*     */   public static String intern(String toIntern)
/*     */   {
/* 178 */     if (DISABLE_INTERNING) {
/* 179 */       return toIntern;
/*     */     }
/*     */     
/* 182 */     if (toIntern == null) {
/* 183 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 187 */     WeakStringEntry checkEntry = new WeakStringEntry(toIntern);
/*     */     
/* 189 */     WeakStringEntry internedEntry = null;
/* 190 */     boolean hit = false;
/*     */     
/* 192 */     managedSetLock.readLock().lock();
/*     */     String internedString;
/*     */     try {
/* 195 */       internedEntry = (WeakStringEntry)managedInterningSet.get(checkEntry);
/*     */       String internedString;
/* 197 */       if ((internedEntry != null) && ((internedString = internedEntry.getString()) != null)) {
/* 198 */         hit = true;
/*     */       }
/*     */       else {
/* 201 */         managedSetLock.readLock().unlock();
/* 202 */         managedSetLock.writeLock().lock();
/*     */         try {
/* 204 */           sanitize(false);
/*     */           
/*     */ 
/* 207 */           internedEntry = (WeakStringEntry)managedInterningSet.get(checkEntry);
/*     */           String internedString;
/* 209 */           if ((internedEntry != null) && ((internedString = internedEntry.getString()) != null)) {
/* 210 */             hit = true;
/*     */           } else {
/* 212 */             toIntern = new String(toIntern);
/* 213 */             checkEntry = new WeakStringEntry(toIntern);
/* 214 */             managedInterningSet.add(checkEntry);
/* 215 */             internedString = toIntern;
/*     */           }
/*     */         } finally {
/* 218 */           managedSetLock.readLock().lock();
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 223 */       managedSetLock.readLock().unlock();
/*     */     }
/*     */     
/* 226 */     if (hit) {
/* 227 */       internedEntry.incHits();
/* 228 */       checkEntry.destroy();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 234 */     return internedString;
/*     */   }
/*     */   
/*     */   public static char[] intern(char[] toIntern)
/*     */   {
/* 239 */     if (DISABLE_INTERNING) {
/* 240 */       return toIntern;
/*     */     }
/*     */     
/* 243 */     if (toIntern == null) {
/* 244 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 248 */     WeakCharArrayEntry checkEntry = new WeakCharArrayEntry(toIntern);
/*     */     
/* 250 */     WeakCharArrayEntry internedEntry = null;
/* 251 */     boolean hit = false;
/*     */     
/* 253 */     managedSetLock.readLock().lock();
/*     */     char[] internedCharArray;
/*     */     try {
/* 256 */       internedEntry = (WeakCharArrayEntry)managedInterningSet.get(checkEntry);
/*     */       char[] internedCharArray;
/* 258 */       if ((internedEntry != null) && ((internedCharArray = internedEntry.getCharArray()) != null)) {
/* 259 */         hit = true;
/*     */       }
/*     */       else {
/* 262 */         managedSetLock.readLock().unlock();
/* 263 */         managedSetLock.writeLock().lock();
/*     */         try {
/* 265 */           sanitize(false);
/*     */           
/*     */ 
/* 268 */           internedEntry = (WeakCharArrayEntry)managedInterningSet.get(checkEntry);
/*     */           char[] internedCharArray;
/* 270 */           if ((internedEntry != null) && ((internedCharArray = internedEntry.getCharArray()) != null)) {
/* 271 */             hit = true;
/*     */           } else {
/* 273 */             managedInterningSet.add(checkEntry);
/* 274 */             internedCharArray = toIntern;
/*     */           }
/*     */         } finally {
/* 277 */           managedSetLock.readLock().lock();
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 282 */       managedSetLock.readLock().unlock();
/*     */     }
/*     */     
/* 285 */     if (hit) {
/* 286 */       System.out.println("hit for " + new String(toIntern));
/* 287 */       internedEntry.incHits();
/* 288 */       checkEntry.destroy();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 294 */     return internedCharArray;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static byte[] internBytes(byte[] toIntern)
/*     */   {
/* 302 */     if (DISABLE_INTERNING) {
/* 303 */       return toIntern;
/*     */     }
/*     */     
/* 306 */     if (toIntern == null) {
/* 307 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 311 */     WeakByteArrayEntry checkEntry = new WeakByteArrayEntry(toIntern);
/*     */     
/* 313 */     WeakByteArrayEntry internedEntry = null;
/* 314 */     boolean hit = false;
/* 315 */     managedSetLock.readLock().lock();
/*     */     byte[] internedArray;
/*     */     try {
/* 318 */       internedEntry = (WeakByteArrayEntry)managedInterningSet.get(checkEntry);
/* 319 */       byte[] internedArray; if ((internedEntry != null) && ((internedArray = internedEntry.getArray()) != null)) {
/* 320 */         hit = true;
/*     */       }
/*     */       else {
/* 323 */         managedSetLock.readLock().unlock();
/* 324 */         managedSetLock.writeLock().lock();
/*     */         try {
/* 326 */           sanitize(false);
/*     */           
/* 328 */           internedEntry = (WeakByteArrayEntry)managedInterningSet.get(checkEntry);
/* 329 */           byte[] internedArray; if ((internedEntry != null) && ((internedArray = internedEntry.getArray()) != null)) {
/* 330 */             hit = true;
/*     */           }
/*     */           else {
/* 333 */             managedInterningSet.add(checkEntry);
/* 334 */             internedArray = toIntern;
/*     */           }
/*     */         } finally {
/* 337 */           managedSetLock.readLock().lock();
/*     */         }
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 343 */       managedSetLock.readLock().unlock();
/*     */     }
/* 345 */     if (hit)
/*     */     {
/* 347 */       internedEntry.incHits();
/* 348 */       checkEntry.destroy();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 354 */     if (!Arrays.equals(toIntern, internedArray)) {
/* 355 */       System.err.println("mismatch");
/*     */     }
/* 357 */     return internedArray;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static File internFile(File toIntern)
/*     */   {
/* 366 */     if (DISABLE_INTERNING) {
/* 367 */       return toIntern;
/*     */     }
/*     */     
/* 370 */     if (toIntern == null) {
/* 371 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 375 */     WeakFileEntry checkEntry = new WeakFileEntry(toIntern);
/*     */     
/* 377 */     WeakFileEntry internedEntry = null;
/* 378 */     boolean hit = false;
/* 379 */     managedSetLock.readLock().lock();
/*     */     File internedFile;
/*     */     try {
/* 382 */       internedEntry = (WeakFileEntry)managedInterningSet.get(checkEntry);
/* 383 */       File internedFile; if ((internedEntry != null) && ((internedFile = internedEntry.getFile()) != null)) {
/* 384 */         hit = true;
/*     */       }
/*     */       else {
/* 387 */         managedSetLock.readLock().unlock();
/* 388 */         managedSetLock.writeLock().lock();
/*     */         try {
/* 390 */           sanitize(false);
/*     */           
/* 392 */           internedEntry = (WeakFileEntry)managedInterningSet.get(checkEntry);
/* 393 */           File internedFile; if ((internedEntry != null) && ((internedFile = internedEntry.getFile()) != null)) {
/* 394 */             hit = true;
/*     */           }
/*     */           else {
/* 397 */             managedInterningSet.add(checkEntry);
/* 398 */             internedFile = toIntern;
/*     */           }
/*     */         } finally {
/* 401 */           managedSetLock.readLock().lock();
/*     */         }
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 407 */       managedSetLock.readLock().unlock();
/*     */     }
/*     */     
/* 410 */     if (hit)
/*     */     {
/* 412 */       internedEntry.incHits();
/* 413 */       checkEntry.destroy();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 419 */     if (!toIntern.equals(internedFile)) {
/* 420 */       System.err.println("mismatch");
/*     */     }
/* 422 */     return internedFile;
/*     */   }
/*     */   
/*     */   public static URL internURL(URL toIntern)
/*     */   {
/* 427 */     if (DISABLE_INTERNING) {
/* 428 */       return toIntern;
/*     */     }
/*     */     
/* 431 */     if (toIntern == null) {
/* 432 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 436 */     WeakURLEntry checkEntry = new WeakURLEntry(toIntern);
/*     */     
/* 438 */     WeakURLEntry internedEntry = null;
/* 439 */     boolean hit = false;
/* 440 */     managedSetLock.readLock().lock();
/*     */     URL internedURL;
/*     */     try {
/* 443 */       internedEntry = (WeakURLEntry)managedInterningSet.get(checkEntry);
/* 444 */       URL internedURL; if ((internedEntry != null) && ((internedURL = internedEntry.getURL()) != null)) {
/* 445 */         hit = true;
/*     */       }
/*     */       else {
/* 448 */         managedSetLock.readLock().unlock();
/* 449 */         managedSetLock.writeLock().lock();
/*     */         try {
/* 451 */           sanitize(false);
/*     */           
/* 453 */           internedEntry = (WeakURLEntry)managedInterningSet.get(checkEntry);
/* 454 */           URL internedURL; if ((internedEntry != null) && ((internedURL = internedEntry.getURL()) != null)) {
/* 455 */             hit = true;
/*     */           }
/*     */           else {
/* 458 */             managedInterningSet.add(checkEntry);
/* 459 */             internedURL = toIntern;
/*     */           }
/*     */         } finally {
/* 462 */           managedSetLock.readLock().lock();
/*     */         }
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 468 */       managedSetLock.readLock().unlock();
/*     */     }
/*     */     
/* 471 */     if (hit)
/*     */     {
/* 473 */       internedEntry.incHits();
/* 474 */       checkEntry.destroy();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 480 */     if (!toIntern.toExternalForm().equals(internedURL.toExternalForm())) {
/* 481 */       System.err.println("mismatch");
/*     */     }
/* 483 */     return internedURL;
/*     */   }
/*     */   
/*     */ 
/* 487 */   private static final Comparator savingsComp = new Comparator()
/*     */   {
/*     */     public int compare(Object o1, Object o2) {
/* 490 */       StringInterner.WeakWeightedEntry w1 = (StringInterner.WeakWeightedEntry)o1;
/* 491 */       StringInterner.WeakWeightedEntry w2 = (StringInterner.WeakWeightedEntry)o2;
/* 492 */       return w1.hits * w1.size - w2.hits * w2.size;
/*     */     }
/*     */   };
/*     */   
/*     */   private static void sanitizeLight()
/*     */   {
/* 498 */     synchronized (unmanagedInterningSet)
/*     */     {
/*     */       WeakEntry ref;
/* 501 */       while ((ref = (WeakEntry)unmanagedRefQueue.poll()) != null) {
/* 502 */         unmanagedInterningSet.remove(ref);
/*     */       }
/* 504 */       unmanagedInterningSet.compactify(-1.0F);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void sanitize(boolean scheduled)
/*     */   {
/*     */     WeakWeightedEntry ref;
/* 511 */     while ((ref = (WeakWeightedEntry)managedRefQueue.poll()) != null)
/*     */     {
/* 513 */       if (!ref.isDestroyed())
/*     */       {
/* 515 */         managedInterningSet.remove(ref);
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 520 */         System.err.println("double removal " + ref);
/*     */       }
/*     */     }
/* 523 */     int currentSetSize = managedInterningSet.size();
/*     */     
/*     */ 
/*     */     Iterator it;
/*     */     
/*     */ 
/* 529 */     if ((currentSetSize >= 2000) || (scheduled))
/*     */     {
/*     */ 
/*     */ 
/* 533 */       ArrayList remaining = new ArrayList();
/*     */       
/* 535 */       for (Iterator it = managedInterningSet.iterator(); it.hasNext();)
/*     */       {
/* 537 */         if ((managedInterningSet.size() < 1500) && (!scheduled))
/*     */           break label350;
/* 539 */         WeakWeightedEntry entry = (WeakWeightedEntry)it.next();
/* 540 */         if (entry.hits == 0)
/*     */         {
/*     */ 
/*     */ 
/* 544 */           it.remove();
/*     */         } else
/* 546 */           remaining.add(entry);
/*     */       }
/* 548 */       currentSetSize = managedInterningSet.size();
/* 549 */       if ((currentSetSize >= 1500) || (!scheduled))
/*     */       {
/* 551 */         if ((currentSetSize >= 1500) || (scheduled))
/*     */         {
/* 553 */           Collections.sort(remaining, savingsComp);
/*     */           
/* 555 */           for (int i = 0; i < remaining.size(); i++)
/*     */           {
/* 557 */             currentSetSize = managedInterningSet.size();
/* 558 */             if ((currentSetSize < 1000) && (scheduled))
/*     */               break;
/* 560 */             if ((currentSetSize < 1500) && (!scheduled))
/*     */               break label350;
/* 562 */             WeakWeightedEntry entry = (WeakWeightedEntry)remaining.get(i);
/*     */             
/*     */ 
/* 565 */             managedInterningSet.remove(entry);
/*     */           }
/*     */         }
/* 568 */       } else { currentSetSize = managedInterningSet.size();
/* 569 */         if ((currentSetSize >= 750) || (!scheduled))
/*     */         {
/* 571 */           if ((currentSetSize >= 1500) || (scheduled))
/*     */           {
/* 573 */             for (it = managedInterningSet.iterator(); it.hasNext();) {
/* 574 */               ((WeakWeightedEntry)it.next()).decHits();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     label350:
/*     */     
/* 584 */     if (scheduled) {
/* 585 */       managedInterningSet.compactify(-1.0F);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class WeakEntry extends WeakReference {
/*     */     private final int hash;
/*     */     
/*     */     protected WeakEntry(Object o, ReferenceQueue q, int hash) {
/* 593 */       super(q);
/* 594 */       this.hash = hash;
/*     */     }
/*     */     
/*     */     public WeakEntry(Object o, ReferenceQueue q)
/*     */     {
/* 599 */       super(q);
/* 600 */       this.hash = o.hashCode();
/*     */     }
/*     */     
/*     */     public boolean equals(Object obj) {
/* 604 */       if (this == obj)
/* 605 */         return true;
/* 606 */       if ((obj instanceof WeakEntry))
/*     */       {
/* 608 */         Object myObj = get();
/* 609 */         Object otherObj = ((WeakEntry)obj).get();
/* 610 */         return myObj == null ? false : myObj.equals(otherObj);
/*     */       }
/* 612 */       return false;
/*     */     }
/*     */     
/*     */     public final int hashCode() {
/* 616 */       return this.hash;
/*     */     }
/*     */   }
/*     */   
/*     */   private static abstract class WeakWeightedEntry extends StringInterner.WeakEntry
/*     */   {
/*     */     final short size;
/*     */     short hits;
/*     */     
/*     */     public WeakWeightedEntry(Object o, int hash, int size) {
/* 626 */       super(StringInterner.managedRefQueue, hash);
/* 627 */       this.size = ((short)(size & 0x7FFF));
/*     */     }
/*     */     
/*     */     public void incHits() {
/* 631 */       if (this.hits < Short.MAX_VALUE)
/* 632 */         this.hits = ((short)(this.hits + 1));
/*     */     }
/*     */     
/*     */     public void decHits() {
/* 636 */       if (this.hits > 0)
/* 637 */         this.hits = ((short)(this.hits - 1));
/*     */     }
/*     */     
/*     */     public String toString() {
/* 641 */       return getClass().getName().replaceAll("^.*\\..\\w+$", "") + " h=" + this.hits + ";s=" + this.size;
/*     */     }
/*     */     
/*     */     public void destroy() {
/* 645 */       this.hits = -1;
/*     */     }
/*     */     
/*     */     public boolean isDestroyed() {
/* 649 */       return this.hits == -1;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class WeakByteArrayEntry extends StringInterner.WeakWeightedEntry
/*     */   {
/*     */     public WeakByteArrayEntry(byte[] array)
/*     */     {
/* 657 */       super(HashCodeUtils.hashCode(array), array.length + 8);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean equals(Object obj)
/*     */     {
/* 664 */       if (this == obj)
/* 665 */         return true;
/* 666 */       if ((obj instanceof WeakByteArrayEntry))
/*     */       {
/* 668 */         byte[] myArray = getArray();
/* 669 */         byte[] otherArray = ((WeakByteArrayEntry)obj).getArray();
/* 670 */         return myArray == null ? false : Arrays.equals(myArray, otherArray);
/*     */       }
/* 672 */       return false;
/*     */     }
/*     */     
/*     */     public byte[] getArray() {
/* 676 */       return (byte[])get();
/*     */     }
/*     */     
/*     */     public String toString() {
/* 680 */       return super.toString() + " " + (getArray() == null ? "null" : new String(getArray()));
/*     */     }
/*     */   }
/*     */   
/*     */   private static class WeakCharArrayEntry extends StringInterner.WeakWeightedEntry
/*     */   {
/*     */     public WeakCharArrayEntry(char[] array)
/*     */     {
/* 688 */       super(HashCodeUtils.hashCode(array), array.length + 8);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean equals(Object obj)
/*     */     {
/* 695 */       if (this == obj)
/* 696 */         return true;
/* 697 */       if ((obj instanceof WeakCharArrayEntry))
/*     */       {
/* 699 */         char[] myArray = getCharArray();
/* 700 */         char[] otherArray = ((WeakCharArrayEntry)obj).getCharArray();
/* 701 */         return myArray == null ? false : Arrays.equals(myArray, otherArray);
/*     */       }
/* 703 */       return false;
/*     */     }
/*     */     
/*     */     public char[] getCharArray() {
/* 707 */       return (char[])get();
/*     */     }
/*     */     
/*     */     public String toString() {
/* 711 */       return super.toString() + " " + (getCharArray() == null ? "null" : new String(getCharArray()));
/*     */     }
/*     */   }
/*     */   
/*     */   private static class WeakStringEntry extends StringInterner.WeakWeightedEntry
/*     */   {
/*     */     public WeakStringEntry(String entry)
/*     */     {
/* 719 */       super(entry.hashCode(), 24 + entry.length() * 2);
/*     */     }
/*     */     
/*     */     public String getString() {
/* 723 */       return (String)get();
/*     */     }
/*     */     
/*     */     public String toString() {
/* 727 */       return super.toString() + " " + getString();
/*     */     }
/*     */   }
/*     */   
/*     */   private static class WeakFileEntry extends StringInterner.WeakWeightedEntry
/*     */   {
/*     */     public WeakFileEntry(File entry)
/*     */     {
/* 735 */       super(entry.hashCode(), 40 + entry.getPath().length() * 2);
/*     */     }
/*     */     
/*     */     public File getFile() {
/* 739 */       return (File)get();
/*     */     }
/*     */     
/*     */     public String toString() {
/* 743 */       return super.toString() + " " + getFile();
/*     */     }
/*     */   }
/*     */   
/*     */   private static class WeakURLEntry
/*     */     extends StringInterner.WeakWeightedEntry
/*     */   {
/*     */     public WeakURLEntry(URL entry)
/*     */     {
/* 752 */       super(entry.toExternalForm().hashCode(), 176 + entry.toString().length() * 2);
/*     */     }
/*     */     
/*     */     public URL getURL() {
/* 756 */       return (URL)get();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean equals(Object obj)
/*     */     {
/* 763 */       if (this == obj)
/* 764 */         return true;
/* 765 */       if ((obj instanceof WeakURLEntry))
/*     */       {
/* 767 */         URL my = getURL();
/* 768 */         URL other = ((WeakURLEntry)obj).getURL();
/*     */         
/* 770 */         if (my == other) {
/* 771 */           return true;
/*     */         }
/* 773 */         if ((my == null) || (other == null)) {
/* 774 */           return false;
/*     */         }
/*     */         
/* 777 */         return my.toExternalForm().equals(other.toExternalForm());
/*     */       }
/* 779 */       return false;
/*     */     }
/*     */     
/*     */     public String toString() {
/* 783 */       return super.toString() + " " + getURL();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/StringInterner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */