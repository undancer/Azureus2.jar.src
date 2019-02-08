/*      */ package org.gudy.azureus2.core3.util;
/*      */ 
/*      */ import java.io.PrintStream;
/*      */ import java.math.BigInteger;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.IdentityHashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.TreeMap;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DirectByteBufferPoolReal
/*      */   extends DirectByteBufferPool
/*      */ {
/*   50 */   private static final boolean disable_gc = System.getProperty("az.disable.explicit.gc", "0").equals("1");
/*      */   
/*      */ 
/*      */ 
/*      */   protected static final boolean DEBUG_TRACK_HANDEDOUT = false;
/*      */   
/*      */ 
/*      */ 
/*      */   protected static final boolean DEBUG_PRINT_MEM = false;
/*      */   
/*      */ 
/*      */ 
/*      */   protected static final int DEBUG_PRINT_TIME = 120000;
/*      */   
/*      */ 
/*      */ 
/*      */   protected static final boolean DEBUG_HANDOUT_SIZES = false;
/*      */   
/*      */ 
/*      */ 
/*      */   protected static final boolean DEBUG_FREE_SIZES = false;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final int START_POWER = 12;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final int END_POWER = 28;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final int[] EXTRA_BUCKETS;
/*      */   
/*      */ 
/*      */ 
/*      */   public static final int MAX_SIZE;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final DirectByteBufferPoolReal pool;
/*      */   
/*      */ 
/*      */ 
/*   94 */   private final Map buffersMap = new LinkedHashMap(17);
/*      */   
/*   96 */   private final Object poolsLock = new Object();
/*      */   private static final int SLICE_END_SIZE = 2048;
/*      */   private static final int SLICE_ALLOC_CHUNK_SIZE = 4096;
/*      */   private static final short[] SLICE_ENTRY_SIZES;
/*      */   private static final short[] SLICE_ALLOC_MAXS;
/*      */   private static final short[] SLICE_ENTRY_ALLOC_SIZES;
/*      */   private static final List[] slice_entries;
/*      */   private static final boolean[][] slice_allocs;
/*      */   private static final boolean[] slice_alloc_fails;
/*      */   
/*      */   static
/*      */   {
/*   54 */     if (disable_gc)
/*      */     {
/*   56 */       System.out.println("Explicit GC disabled");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   86 */     EXTRA_BUCKETS = new int[] { 16512 };
/*      */     
/*      */ 
/*   89 */     MAX_SIZE = BigInteger.valueOf(2L).pow(28).intValue();
/*      */     
/*   91 */     pool = new DirectByteBufferPoolReal();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  102 */     SLICE_ENTRY_SIZES = new short[] { 8, 16, 32, 64, 128, 256, 512, 1024, 2048 };
/*  103 */     SLICE_ALLOC_MAXS = new short[] { 256, 256, 128, 64, 64, 64, 64, 64, 64 };
/*      */     
/*  105 */     SLICE_ENTRY_ALLOC_SIZES = new short[SLICE_ENTRY_SIZES.length];
/*  106 */     slice_entries = new List[SLICE_ENTRY_SIZES.length];
/*  107 */     slice_allocs = new boolean[SLICE_ENTRY_SIZES.length][];
/*  108 */     slice_alloc_fails = new boolean[SLICE_ENTRY_SIZES.length];
/*      */     
/*      */ 
/*      */ 
/*  112 */     int mult = COConfigurationManager.getIntParameter("memory.slice.limit.multiplier");
/*      */     
/*  114 */     if (mult > 1)
/*      */     {
/*  116 */       for (int i = 0; i < SLICE_ALLOC_MAXS.length; i++)
/*      */       {
/*  118 */         int tmp249_248 = i; short[] tmp249_245 = SLICE_ALLOC_MAXS;tmp249_245[tmp249_248] = ((short)(tmp249_245[tmp249_248] * mult));
/*      */       }
/*      */     }
/*      */     
/*  122 */     for (int i = 0; i < SLICE_ENTRY_SIZES.length; i++)
/*      */     {
/*  124 */       SLICE_ENTRY_ALLOC_SIZES[i] = ((short)(4096 / SLICE_ENTRY_SIZES[i]));
/*      */       
/*  126 */       slice_allocs[i] = new boolean[SLICE_ALLOC_MAXS[i]];
/*      */       
/*  128 */       slice_entries[i] = new LinkedList();
/*      */     }
/*      */   }
/*      */   
/*  132 */   private static final long[] slice_use_count = new long[SLICE_ENTRY_SIZES.length];
/*      */   
/*  134 */   private final Map handed_out = new IdentityHashMap();
/*      */   
/*  136 */   private final Map size_counts = new TreeMap();
/*      */   
/*      */   private static final long COMPACTION_CHECK_PERIOD = 120000L;
/*      */   
/*      */   private static final long MAX_FREE_BYTES = 10485760L;
/*      */   private static final long MIN_FREE_BYTES = 1048576L;
/*  142 */   private long bytesIn = 0L;
/*  143 */   private long bytesOut = 0L;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DirectByteBufferPoolReal()
/*      */   {
/*  151 */     ArrayList list = new ArrayList();
/*      */     
/*  153 */     for (int p = 12; p <= 28; p++)
/*      */     {
/*  155 */       list.add(new Integer(BigInteger.valueOf(2L).pow(p).intValue()));
/*      */     }
/*      */     
/*  158 */     for (int i = 0; i < EXTRA_BUCKETS.length; i++)
/*      */     {
/*  160 */       list.add(new Integer(EXTRA_BUCKETS[i]));
/*      */     }
/*      */     
/*  163 */     Integer[] sizes = new Integer[list.size()];
/*  164 */     list.toArray(sizes);
/*  165 */     Arrays.sort(sizes);
/*      */     
/*  167 */     for (int i = 0; i < sizes.length; i++)
/*      */     {
/*  169 */       ArrayList bufferPool = new ArrayList();
/*      */       
/*  171 */       this.buffersMap.put(sizes[i], bufferPool);
/*      */     }
/*      */     
/*      */ 
/*  175 */     SimpleTimer.addPeriodicEvent("DirectBB:compact", 120000L, new TimerEventPerformer()
/*      */     {
/*      */ 
/*      */       public void perform(TimerEvent ev)
/*      */       {
/*      */ 
/*  181 */         DirectByteBufferPoolReal.this.compactBuffers();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private ByteBuffer allocateNewBuffer(int _size)
/*      */   {
/*      */     try
/*      */     {
/*  205 */       return ByteBuffer.allocateDirect(_size);
/*      */ 
/*      */     }
/*      */     catch (OutOfMemoryError e)
/*      */     {
/*  210 */       clearBufferPools();
/*      */       
/*  212 */       runGarbageCollection();
/*      */       try
/*      */       {
/*  215 */         return ByteBuffer.allocateDirect(_size);
/*      */       }
/*      */       catch (OutOfMemoryError ex)
/*      */       {
/*  219 */         String msg = "Memory allocation failed: Out of direct memory space.\nTo fix: Use the -XX:MaxDirectMemorySize=512m command line option,\nor upgrade your Java JRE to version 1.4.2_05 or 1.5 series or newer.";
/*      */         
/*      */ 
/*  222 */         Debug.out(msg);
/*      */         
/*  224 */         Logger.log(new LogAlert(false, 3, msg));
/*      */         
/*  226 */         printInUse(true);
/*      */         
/*  228 */         throw ex;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DirectByteBuffer getBufferSupport(byte _allocator, int _length)
/*      */   {
/*  243 */     if (_length < 1) {
/*  244 */       Debug.out("requested length [" + _length + "] < 1");
/*  245 */       return null;
/*      */     }
/*      */     
/*  248 */     if (_length > MAX_SIZE) {
/*  249 */       Debug.out("requested length [" + _length + "] > MAX_SIZE [" + MAX_SIZE + "]");
/*  250 */       return null;
/*      */     }
/*      */     
/*  253 */     return pool.getBufferHelper(_allocator, _length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private DirectByteBuffer getBufferHelper(byte _allocator, int _length)
/*      */   {
/*      */     DirectByteBuffer res;
/*      */     
/*      */ 
/*      */ 
/*      */     DirectByteBuffer res;
/*      */     
/*      */ 
/*      */ 
/*  270 */     if (_length <= 2048)
/*      */     {
/*  272 */       res = getSliceBuffer(_allocator, _length);
/*      */     }
/*      */     else
/*      */     {
/*  276 */       ByteBuffer buff = null;
/*      */       
/*  278 */       Integer reqVal = new Integer(_length);
/*      */       
/*      */ 
/*      */ 
/*  282 */       Iterator it = this.buffersMap.keySet().iterator();
/*      */       
/*  284 */       while (it.hasNext())
/*      */       {
/*  286 */         Integer keyVal = (Integer)it.next();
/*      */         
/*      */ 
/*      */ 
/*  290 */         if (reqVal.compareTo(keyVal) <= 0)
/*      */         {
/*  292 */           ArrayList bufferPool = (ArrayList)this.buffersMap.get(keyVal);
/*      */           
/*      */           for (;;)
/*      */           {
/*  296 */             synchronized (this.poolsLock)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  302 */               if (bufferPool.isEmpty())
/*      */               {
/*  304 */                 buff = allocateNewBuffer(keyVal.intValue());
/*      */                 
/*  306 */                 if (buff == null)
/*      */                 {
/*  308 */                   Debug.out("allocateNewBuffer for " + _length + " returned null");
/*      */                 }
/*      */                 
/*  311 */                 break;
/*      */               }
/*      */               
/*      */ 
/*  315 */               synchronized (bufferPool)
/*      */               {
/*  317 */                 buff = (ByteBuffer)bufferPool.remove(bufferPool.size() - 1);
/*      */               }
/*      */               
/*  320 */               if (buff == null)
/*      */               {
/*  322 */                 Debug.out("buffer pool for " + _length + " contained null entry");
/*      */               } else {
/*      */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  336 */       if (buff == null)
/*      */       {
/*  338 */         String str = "Unable to find an appropriate buffer pool for " + _length;
/*      */         
/*  340 */         Debug.out(str);
/*      */         
/*  342 */         throw new RuntimeException(str);
/*      */       }
/*      */       
/*  345 */       res = new DirectByteBuffer(_allocator, buff, this);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  350 */     ByteBuffer buff = res.getBufferInternal();
/*      */     
/*  352 */     buff.clear();
/*      */     
/*  354 */     buff.limit(_length);
/*      */     
/*  356 */     this.bytesOut += buff.capacity();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  401 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void returnBufferSupport(DirectByteBuffer ddb)
/*      */   {
/*  413 */     ByteBuffer buff = ddb.getBufferInternal();
/*      */     
/*  415 */     if (buff == null)
/*      */     {
/*  417 */       Debug.out("Returned dbb has null delegate");
/*      */       
/*  419 */       throw new RuntimeException("Returned dbb has null delegate");
/*      */     }
/*      */     
/*  422 */     int capacity = buff.capacity();
/*      */     
/*  424 */     this.bytesIn += capacity;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  443 */     if (capacity <= 2048)
/*      */     {
/*  445 */       freeSliceBuffer(ddb);
/*      */     }
/*      */     else {
/*  448 */       Integer buffSize = new Integer(capacity);
/*      */       
/*  450 */       ArrayList bufferPool = (ArrayList)this.buffersMap.get(buffSize);
/*      */       
/*  452 */       if (bufferPool != null)
/*      */       {
/*      */ 
/*      */ 
/*  456 */         synchronized (bufferPool)
/*      */         {
/*  458 */           bufferPool.add(buff);
/*      */         }
/*      */         
/*      */       } else {
/*  462 */         Debug.out("Invalid buffer given; could not find proper buffer pool");
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void clearBufferPools()
/*      */   {
/*  473 */     Iterator it = this.buffersMap.values().iterator();
/*  474 */     while (it.hasNext()) {
/*  475 */       ArrayList bufferPool = (ArrayList)it.next();
/*  476 */       bufferPool.clear();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void runGarbageCollection()
/*      */   {
/*  485 */     if (!disable_gc)
/*      */     {
/*      */ 
/*      */ 
/*  489 */       System.runFinalization();
/*  490 */       System.gc();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void compactBuffers()
/*      */   {
/*  501 */     synchronized (this.poolsLock)
/*      */     {
/*  503 */       long freeSize = bytesFree();
/*      */       
/*  505 */       if (freeSize >= 1048576L)
/*      */       {
/*      */         float remainingFactor;
/*      */         
/*      */         float remainingFactor;
/*  510 */         if (freeSize > 10485760L) {
/*  511 */           remainingFactor = 5242880.0F / (float)freeSize;
/*      */         } else {
/*  513 */           remainingFactor = 1.0F - 0.5F * (float)freeSize / 1.048576E7F;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  518 */         ArrayList pools = new ArrayList(this.buffersMap.values());
/*  519 */         for (int i = pools.size() - 1; i >= 0; i--)
/*      */         {
/*  521 */           ArrayList pool = (ArrayList)pools.get(i);
/*  522 */           int limit = (int)(pool.size() * remainingFactor);
/*  523 */           for (int j = pool.size() - 1; j >= limit; j--) {
/*  524 */             pool.remove(j);
/*      */           }
/*      */         }
/*  527 */         runGarbageCollection();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  536 */     compactSlices();
/*      */   }
/*      */   
/*      */ 
/*      */   private long bytesFree()
/*      */   {
/*  542 */     long bytesUsed = 0L;
/*  543 */     synchronized (this.poolsLock)
/*      */     {
/*  545 */       Iterator it = this.buffersMap.keySet().iterator();
/*  546 */       while (it.hasNext()) {
/*  547 */         Integer keyVal = (Integer)it.next();
/*  548 */         ArrayList bufferPool = (ArrayList)this.buffersMap.get(keyVal);
/*      */         
/*  550 */         bytesUsed += keyVal.intValue() * bufferPool.size();
/*      */       }
/*      */     }
/*  553 */     return bytesUsed;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private DirectByteBuffer getSliceBuffer(byte _allocator, int _length)
/*      */   {
/*  797 */     int slice_index = getSliceIndex(_length);
/*      */     
/*  799 */     List my_slice_entries = slice_entries[slice_index];
/*      */     
/*  801 */     synchronized (my_slice_entries)
/*      */     {
/*  803 */       boolean[] my_allocs = slice_allocs[slice_index];
/*      */       
/*  805 */       sliceBuffer sb = null;
/*      */       
/*  807 */       if (my_slice_entries.size() > 0)
/*      */       {
/*  809 */         sb = (sliceBuffer)my_slice_entries.remove(0);
/*      */         
/*  811 */         slice_use_count[slice_index] += 1L;
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*  817 */         short slot = -1;
/*      */         
/*  819 */         for (short i = 0; i < my_allocs.length; i = (short)(i + 1))
/*      */         {
/*  821 */           if (my_allocs[i] == 0)
/*      */           {
/*  823 */             slot = i;
/*      */             
/*  825 */             break;
/*      */           }
/*      */         }
/*      */         
/*  829 */         if (slot != -1)
/*      */         {
/*  831 */           short slice_entry_size = SLICE_ENTRY_SIZES[slice_index];
/*  832 */           short slice_entry_count = SLICE_ENTRY_ALLOC_SIZES[slice_index];
/*      */           
/*  834 */           ByteBuffer chunk = ByteBuffer.allocateDirect(slice_entry_size * slice_entry_count);
/*      */           
/*  836 */           my_allocs[slot] = true;
/*      */           
/*  838 */           for (short i = 0; i < slice_entry_count; i = (short)(i + 1))
/*      */           {
/*  840 */             chunk.limit((i + 1) * slice_entry_size);
/*  841 */             chunk.position(i * slice_entry_size);
/*      */             
/*  843 */             ByteBuffer slice = chunk.slice();
/*      */             
/*  845 */             sliceBuffer new_buffer = new sliceBuffer(slice, slot, i);
/*      */             
/*  847 */             if (i == 0)
/*      */             {
/*  849 */               sb = new_buffer;
/*      */               
/*  851 */               slice_use_count[slice_index] += 1L;
/*      */             }
/*      */             else
/*      */             {
/*  855 */               my_slice_entries.add(new_buffer);
/*      */             }
/*      */           }
/*      */         }
/*      */         else {
/*  860 */           if (slice_alloc_fails[slice_index] == 0)
/*      */           {
/*  862 */             slice_alloc_fails[slice_index] = true;
/*      */             
/*  864 */             Debug.out("Run out of slice space for '" + SLICE_ENTRY_SIZES[slice_index] + ", reverting to normal allocation");
/*      */           }
/*      */           
/*  867 */           ByteBuffer buff = ByteBuffer.allocate(_length);
/*      */           
/*  869 */           return new DirectByteBuffer(_allocator, buff, this);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  874 */       sliceDBB dbb = new sliceDBB(_allocator, sb);
/*      */       
/*  876 */       return dbb;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void freeSliceBuffer(DirectByteBuffer ddb)
/*      */   {
/*  884 */     if ((ddb instanceof sliceDBB))
/*      */     {
/*  886 */       int slice_index = getSliceIndex(ddb.getBufferInternal().capacity());
/*      */       
/*  888 */       List my_slice_entries = slice_entries[slice_index];
/*      */       
/*  890 */       synchronized (my_slice_entries)
/*      */       {
/*  892 */         my_slice_entries.add(0, ((sliceDBB)ddb).getSliceBuffer());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void compactSlices()
/*      */   {
/*  909 */     for (int i = 0; i < slice_entries.length; i++)
/*      */     {
/*  911 */       int entries_per_alloc = SLICE_ENTRY_ALLOC_SIZES[i];
/*      */       
/*  913 */       List l = slice_entries[i];
/*      */       
/*      */ 
/*      */ 
/*  917 */       if (l.size() >= entries_per_alloc)
/*      */       {
/*  919 */         synchronized (l)
/*      */         {
/*  921 */           Collections.sort(l, new Comparator()
/*      */           {
/*      */ 
/*      */ 
/*      */             public int compare(Object o1, Object o2)
/*      */             {
/*      */ 
/*      */ 
/*  929 */               DirectByteBufferPoolReal.sliceBuffer sb1 = (DirectByteBufferPoolReal.sliceBuffer)o1;
/*  930 */               DirectByteBufferPoolReal.sliceBuffer sb2 = (DirectByteBufferPoolReal.sliceBuffer)o2;
/*      */               
/*  932 */               int res = sb1.getAllocID() - sb2.getAllocID();
/*      */               
/*  934 */               if (res == 0)
/*      */               {
/*  936 */                 res = sb1.getSliceID() - sb2.getSliceID();
/*      */               }
/*      */               
/*  939 */               return res;
/*      */             }
/*      */             
/*  942 */           });
/*  943 */           boolean[] allocs = slice_allocs[i];
/*      */           
/*  945 */           Iterator it = l.iterator();
/*      */           
/*  947 */           int current_alloc = -1;
/*  948 */           int entry_count = 0;
/*      */           
/*  950 */           boolean freed_one = false;
/*      */           
/*  952 */           while (it.hasNext())
/*      */           {
/*  954 */             sliceBuffer sb = (sliceBuffer)it.next();
/*      */             
/*  956 */             int aid = sb.getAllocID();
/*      */             
/*  958 */             if (aid != current_alloc)
/*      */             {
/*  960 */               if (entry_count == entries_per_alloc)
/*      */               {
/*      */ 
/*      */ 
/*  964 */                 freed_one = true;
/*      */                 
/*  966 */                 allocs[aid] = false;
/*      */               }
/*      */               
/*  969 */               current_alloc = aid;
/*      */               
/*  971 */               entry_count = 1;
/*      */             }
/*      */             else
/*      */             {
/*  975 */               entry_count++;
/*      */             }
/*      */           }
/*      */           
/*  979 */           if (entry_count == entries_per_alloc)
/*      */           {
/*      */ 
/*      */ 
/*  983 */             freed_one = true;
/*      */             
/*  985 */             allocs[current_alloc] = false;
/*      */           }
/*      */           
/*  988 */           if (freed_one)
/*      */           {
/*  990 */             it = l.iterator();
/*      */             
/*  992 */             while (it.hasNext())
/*      */             {
/*  994 */               sliceBuffer sb = (sliceBuffer)it.next();
/*      */               
/*  996 */               if (allocs[sb.getAllocID()] == 0)
/*      */               {
/*  998 */                 it.remove();
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private int getSliceIndex(int _length)
/*      */   {
/* 1011 */     for (int i = 0; i < SLICE_ENTRY_SIZES.length; i++)
/*      */     {
/* 1013 */       if (_length <= SLICE_ENTRY_SIZES[i])
/*      */       {
/* 1015 */         return i;
/*      */       }
/*      */     }
/*      */     
/* 1019 */     Debug.out("eh?");
/*      */     
/* 1021 */     return 0;
/*      */   }
/*      */   
/*      */   private void printInUse(boolean verbose) {}
/*      */   
/*      */   private static class myInteger
/*      */   {
/*      */     int value;
/*      */   }
/*      */   
/*      */   private static class sliceBuffer {
/*      */     private final ByteBuffer buffer;
/*      */     private final short alloc_id;
/*      */     private final short slice_id;
/*      */     
/*      */     protected sliceBuffer(ByteBuffer _buffer, short _alloc_id, short _slice_id) {
/* 1037 */       this.buffer = _buffer;
/* 1038 */       this.alloc_id = _alloc_id;
/* 1039 */       this.slice_id = _slice_id;
/*      */     }
/*      */     
/*      */ 
/*      */     protected ByteBuffer getBuffer()
/*      */     {
/* 1045 */       return this.buffer;
/*      */     }
/*      */     
/*      */ 
/*      */     protected short getAllocID()
/*      */     {
/* 1051 */       return this.alloc_id;
/*      */     }
/*      */     
/*      */ 
/*      */     protected short getSliceID()
/*      */     {
/* 1057 */       return this.slice_id;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static class sliceDBB
/*      */     extends DirectByteBuffer
/*      */   {
/*      */     private final DirectByteBufferPoolReal.sliceBuffer slice_buffer;
/*      */     
/*      */ 
/*      */ 
/*      */     protected sliceDBB(byte _allocator, DirectByteBufferPoolReal.sliceBuffer _sb)
/*      */     {
/* 1072 */       super(_sb.getBuffer(), DirectByteBufferPoolReal.pool);
/*      */       
/* 1074 */       this.slice_buffer = _sb;
/*      */     }
/*      */     
/*      */ 
/*      */     protected DirectByteBufferPoolReal.sliceBuffer getSliceBuffer()
/*      */     {
/* 1080 */       return this.slice_buffer;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/DirectByteBufferPoolReal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */