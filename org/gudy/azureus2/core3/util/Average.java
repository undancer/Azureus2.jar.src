/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Average
/*     */ {
/*     */   private final int refreshRate;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final int period;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final int nbElements;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private long lastUpdate;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private long[] values;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Average(int _refreshRate, int _period)
/*     */   {
/*  57 */     this.refreshRate = _refreshRate;
/*  58 */     this.period = _period;
/*     */     
/*  60 */     this.nbElements = (_period * 1000 / _refreshRate + 2);
/*  61 */     this.lastUpdate = (getEffectiveTime() / _refreshRate);
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
/*     */   public static Average getInstance(int refreshRate, int period)
/*     */   {
/*  74 */     if (refreshRate < 100)
/*  75 */       return null;
/*  76 */     if (period * 1000 < refreshRate)
/*  77 */       return null;
/*  78 */     return new Average(refreshRate, period);
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void clear()
/*     */   {
/*  84 */     this.values = null;
/*  85 */     this.lastUpdate = (getEffectiveTime() / this.refreshRate);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void cloneFrom(Average other)
/*     */   {
/*  92 */     Object[] details = other.getCloneDetails();
/*     */     
/*  94 */     this.values = ((long[])details[0]);
/*  95 */     this.lastUpdate = ((Long)details[1]).longValue();
/*     */   }
/*     */   
/*     */ 
/*     */   private synchronized Object[] getCloneDetails()
/*     */   {
/* 101 */     return new Object[] { this.values, new Long(this.lastUpdate) };
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
/*     */   private void update(long timeFactor)
/*     */   {
/* 115 */     if (this.lastUpdate < timeFactor - this.nbElements) {
/* 116 */       this.lastUpdate = (timeFactor - this.nbElements - 1L);
/*     */     }
/* 118 */     if (this.values != null)
/*     */     {
/*     */ 
/*     */ 
/* 122 */       for (long i = this.lastUpdate + 1L; i <= timeFactor; i += 1L)
/*     */       {
/* 124 */         this.values[((int)(i % this.nbElements))] = 0L;
/*     */       }
/*     */       
/* 127 */       this.values[((int)((timeFactor + 1L) % this.nbElements))] = 0L;
/*     */     }
/*     */     
/*     */ 
/* 131 */     this.lastUpdate = timeFactor;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void addValue(long value)
/*     */   {
/* 140 */     if ((this.values == null) && (value != 0L))
/* 141 */       this.values = new long[this.nbElements];
/* 142 */     if (this.values != null)
/*     */     {
/*     */ 
/* 145 */       long timeFactor = getEffectiveTime() / this.refreshRate;
/*     */       
/* 147 */       update(timeFactor);
/*     */       
/* 149 */       this.values[((int)(timeFactor % this.nbElements))] += value;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getAverage()
/*     */   {
/* 159 */     return getSum() / this.period;
/*     */   }
/*     */   
/*     */   public double getDoubleAverage() {
/* 163 */     return getSum() / this.period;
/*     */   }
/*     */   
/*     */   public String getDoubleAverageAsString(int precision) {
/* 167 */     return DisplayFormatters.formatDecimal(getDoubleAverage(), precision);
/*     */   }
/*     */   
/*     */   public long getAverage(int average_period)
/*     */   {
/* 172 */     int slots = average_period <= 0 ? this.nbElements - 2 : average_period / this.refreshRate;
/*     */     
/* 174 */     if (slots <= 0)
/*     */     {
/* 176 */       slots = 1;
/*     */     }
/* 178 */     else if (slots > this.nbElements - 2)
/*     */     {
/* 180 */       slots = this.nbElements - 2;
/*     */     }
/*     */     
/* 183 */     if (slots == 1)
/*     */     {
/* 185 */       return getPointValue();
/*     */     }
/*     */     
/* 188 */     long res = getSum(slots) / (this.period * slots / (this.nbElements - 2));
/*     */     
/* 190 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized long getPointValue()
/*     */   {
/* 196 */     long timeFactor = getEffectiveTime() / this.refreshRate;
/*     */     
/* 198 */     update(timeFactor);
/*     */     
/* 200 */     return this.values != null ? this.values[((int)((timeFactor - 1L) % this.nbElements))] : 0L;
/*     */   }
/*     */   
/*     */   protected final synchronized long getSum()
/*     */   {
/* 205 */     long sum = 0L;
/*     */     
/* 207 */     if (this.values != null)
/*     */     {
/*     */ 
/* 210 */       long timeFactor = getEffectiveTime() / this.refreshRate;
/*     */       
/* 212 */       update(timeFactor);
/*     */       
/*     */ 
/* 215 */       for (long i = timeFactor + 2L; i < timeFactor + this.nbElements; i += 1L)
/*     */       {
/* 217 */         sum += this.values[((int)(i % this.nbElements))];
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 223 */     return sum;
/*     */   }
/*     */   
/*     */   protected final synchronized long getSum(int slots)
/*     */   {
/* 228 */     long timeFactor = getEffectiveTime() / this.refreshRate;
/*     */     
/* 230 */     update(timeFactor);
/*     */     
/*     */ 
/* 233 */     long sum = 0L;
/*     */     
/* 235 */     if (slots < 1)
/*     */     {
/* 237 */       slots = 1;
/*     */     }
/* 239 */     else if (slots > this.nbElements - 2)
/*     */     {
/* 241 */       slots = this.nbElements - 2;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 247 */     long end_slot = timeFactor + this.nbElements;
/* 248 */     long start_slot = end_slot - slots;
/*     */     
/* 250 */     if (this.values != null) {
/* 251 */       for (long i = start_slot; i < end_slot; i += 1L)
/*     */       {
/* 253 */         sum += this.values[((int)(i % this.nbElements))];
/*     */       }
/*     */     }
/*     */     
/* 257 */     return sum;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getEffectiveTime()
/*     */   {
/* 263 */     return SystemTime.getSteppedMonotonousTime();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/Average.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */