/*     */ package com.aelitis.azureus.core.util.bloom.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.bloom.BloomFilter;
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Random;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public abstract class BloomFilterImpl
/*     */   implements BloomFilter
/*     */ {
/*     */   protected static final String MY_PACKAGE = "com.aelitis.azureus.core.util.bloom.impl";
/*     */   private static final int HASH_NUM = 5;
/*     */   private static final int a2 = 2;
/*     */   private static final int a3 = 3;
/*     */   private static final int a4 = 5;
/*     */   private static final int b2 = 51;
/*     */   private static final int b3 = 145;
/*     */   private static final int b4 = 216;
/*     */   private final int max_entries;
/*     */   private int entry_count;
/*     */   
/*     */   public static BloomFilter deserialiseFromMap(Map<String, Object> map)
/*     */   {
/*  69 */     String impl = MapUtils.getMapString(map, "_impl", "");
/*     */     
/*  71 */     if (impl.startsWith("."))
/*     */     {
/*  73 */       impl = "com.aelitis.azureus.core.util.bloom.impl" + impl;
/*     */     }
/*     */     try
/*     */     {
/*  77 */       Class<BloomFilterImpl> cla = Class.forName(impl);
/*     */       
/*  79 */       Constructor<BloomFilterImpl> cons = cla.getDeclaredConstructor(new Class[] { Map.class });
/*     */       
/*  81 */       cons.setAccessible(true);
/*     */       
/*  83 */       return (BloomFilter)cons.newInstance(new Object[] { map });
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  87 */       Debug.out("Can't construct bloom filter for " + impl, e);
/*     */     }
/*  89 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  99 */   private long start_time = SystemTime.getMonotonousTime();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BloomFilterImpl(int _max_entries)
/*     */   {
/* 107 */     this.max_entries = (_max_entries / 2 * 2 + 1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public BloomFilterImpl(Map<String, Object> x)
/*     */   {
/* 114 */     this.max_entries = ((Long)x.get("_max")).intValue();
/*     */     
/* 116 */     this.entry_count = ((Long)x.get("_count")).intValue();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void serialiseToMap(Map<String, Object> x)
/*     */   {
/* 122 */     String cla = getClass().getName();
/*     */     
/* 124 */     if (cla.startsWith("com.aelitis.azureus.core.util.bloom.impl"))
/*     */     {
/* 126 */       cla = cla.substring("com.aelitis.azureus.core.util.bloom.impl".length());
/*     */     }
/*     */     
/* 129 */     x.put("_impl", cla);
/*     */     
/* 131 */     x.put("_max", new Long(this.max_entries));
/* 132 */     x.put("_count", new Long(this.entry_count));
/*     */   }
/*     */   
/*     */ 
/*     */   public Map<String, Object> serialiseToMap()
/*     */   {
/* 138 */     Map<String, Object> m = new HashMap();
/*     */     
/* 140 */     serialiseToMap(m);
/*     */     
/* 142 */     return m;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getMaxEntries()
/*     */   {
/* 148 */     return this.max_entries;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int add(byte[] value)
/*     */   {
/* 155 */     return add(bytesToInteger(value));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int remove(byte[] value)
/*     */   {
/* 162 */     return remove(bytesToInteger(value));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int count(byte[] value)
/*     */   {
/* 169 */     return count(bytesToInteger(value));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean contains(byte[] value)
/*     */   {
/* 176 */     return contains(bytesToInteger(value));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int add(int value)
/*     */   {
/* 185 */     int count = 65535;
/*     */     
/* 187 */     for (int i = 0; i < 5; i++)
/*     */     {
/* 189 */       int index = getHash(i, value);
/*     */       
/*     */ 
/*     */ 
/* 193 */       int v = incValue(index);
/*     */       
/* 195 */       if (v < count)
/*     */       {
/* 197 */         count = v;
/*     */       }
/*     */     }
/*     */     
/* 201 */     if (count == 0)
/*     */     {
/* 203 */       this.entry_count += 1;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 208 */     return trimValue(count + 1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected int remove(int value)
/*     */   {
/* 215 */     int count = 65535;
/*     */     
/* 217 */     for (int i = 0; i < 5; i++)
/*     */     {
/* 219 */       int index = getHash(i, value);
/*     */       
/*     */ 
/*     */ 
/* 223 */       int v = decValue(index);
/*     */       
/* 225 */       if (v < count)
/*     */       {
/* 227 */         count = v;
/*     */       }
/*     */     }
/*     */     
/* 231 */     if ((count == 1) && (this.entry_count > 0))
/*     */     {
/* 233 */       this.entry_count -= 1;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 238 */     return trimValue(count - 1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected int count(int value)
/*     */   {
/* 245 */     int count = 65535;
/*     */     
/* 247 */     for (int i = 0; i < 5; i++)
/*     */     {
/* 249 */       int index = getHash(i, value);
/*     */       
/* 251 */       int v = getValue(index);
/*     */       
/* 253 */       if (v < count)
/*     */       {
/* 255 */         count = v;
/*     */       }
/*     */     }
/*     */     
/* 259 */     return count;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean contains(int value)
/*     */   {
/* 266 */     for (int i = 0; i < 5; i++)
/*     */     {
/* 268 */       int index = getHash(i, value);
/*     */       
/* 270 */       int v = getValue(index);
/*     */       
/* 272 */       if (v == 0)
/*     */       {
/* 274 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 278 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract int getValue(int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract int incValue(int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */   protected abstract int decValue(int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */   protected abstract int trimValue(int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */   protected int getHash(int function, int value)
/*     */   {
/*     */     long res;
/*     */     
/*     */ 
/* 306 */     switch (function)
/*     */     {
/*     */ 
/*     */ 
/*     */     case 0: 
/* 311 */       res = value;
/*     */       
/* 313 */       break;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     case 1: 
/* 319 */       res = value * value;
/*     */       
/* 321 */       break;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     case 2: 
/* 327 */       res = value * 2 + 51;
/*     */       
/* 329 */       break;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     case 3: 
/* 335 */       res = value * 3 + 145;
/*     */       
/* 337 */       break;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     case 4: 
/* 344 */       res = value * 5 + 216;
/*     */       
/* 346 */       break;
/*     */     
/*     */ 
/*     */     default: 
/* 350 */       System.out.println("**** BloomFilter hash function doesn't exist ****");
/*     */       
/* 352 */       res = 0L;
/*     */     }
/*     */     
/*     */     
/*     */ 
/*     */ 
/* 358 */     return Math.abs((int)res % this.max_entries);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected int bytesToInteger(byte[] data)
/*     */   {
/* 365 */     int res = 1375186049;
/*     */     
/* 367 */     for (int i = 0; i < data.length; i++)
/*     */     {
/*     */ 
/*     */ 
/* 371 */       res = res * 191 + (data[i] & 0xFF);
/*     */     }
/*     */     
/* 374 */     return res;
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
/*     */   public int getEntryCount()
/*     */   {
/* 545 */     return this.entry_count;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSize()
/*     */   {
/* 551 */     return this.max_entries;
/*     */   }
/*     */   
/*     */ 
/*     */   public void clear()
/*     */   {
/* 557 */     this.start_time = SystemTime.getMonotonousTime();
/*     */     
/* 559 */     this.entry_count = 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getStartTimeMono()
/*     */   {
/* 565 */     return this.start_time;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static byte[] getSerialization(byte[] address, int port)
/*     */   {
/* 574 */     byte[] full_address = new byte[address.length + 2];
/* 575 */     System.arraycopy(address, 0, full_address, 0, address.length);
/* 576 */     full_address[address.length] = ((byte)(port >> 8));
/* 577 */     full_address[(address.length + 1)] = ((byte)(port & 0xFF));
/* 578 */     return full_address;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 584 */     return "ent=" + this.entry_count + ",max=" + this.max_entries;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 591 */     Random rand = new Random();
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
/* 630 */     int fp_count = 0;
/*     */     
/* 632 */     for (int j = 0; j < 1000; j++)
/*     */     {
/* 634 */       long start = System.currentTimeMillis();
/*     */       
/* 636 */       BloomFilter b = new BloomFilterAddRemove8Bit(10000);
/*     */       
/*     */ 
/* 639 */       int fp = 0;
/*     */       
/* 641 */       for (int i = 0; i < 1000; i++)
/*     */       {
/*     */ 
/*     */ 
/* 645 */         byte[] key = new byte[6];
/*     */         
/* 647 */         rand.nextBytes(key);
/*     */         
/*     */ 
/*     */ 
/* 651 */         if (i % 2 == 0)
/*     */         {
/* 653 */           b.add(key);
/*     */           
/* 655 */           if (!b.contains(key))
/*     */           {
/* 657 */             System.out.println("false negative on add!!!!");
/*     */           }
/*     */           
/*     */         }
/* 661 */         else if (b.contains(key))
/*     */         {
/* 663 */           fp++;
/*     */         }
/*     */       }
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
/* 680 */       System.out.println("" + (System.currentTimeMillis() - start) + ", fp = " + fp);
/*     */       
/* 682 */       if (fp > 0)
/*     */       {
/* 684 */         fp_count++;
/*     */       }
/*     */     }
/*     */     
/* 688 */     System.out.println(fp_count);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/bloom/impl/BloomFilterImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */