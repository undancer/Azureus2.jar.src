/*     */ package com.aelitis.azureus.core.util.average;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MovingImmediateAverage
/*     */   implements Average
/*     */ {
/*     */   private final int periods;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private double[] data;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  28 */   private int pos = 0;
/*     */   
/*     */ 
/*     */   private double total;
/*     */   
/*     */ 
/*     */   public MovingImmediateAverage(int periods)
/*     */   {
/*  36 */     this.periods = periods;
/*  37 */     this.data = new double[periods];
/*     */   }
/*     */   
/*     */ 
/*     */   public void reset()
/*     */   {
/*  43 */     this.pos = 0;
/*  44 */     this.total = 0.0D;
/*  45 */     this.data = new double[this.periods];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public double update(double newValue)
/*     */   {
/*  53 */     this.total -= this.data[(this.pos % this.periods)];
/*  54 */     this.total += newValue;
/*     */     
/*  56 */     this.data[(this.pos++ % this.periods)] = newValue;
/*     */     
/*  58 */     if (this.pos == Integer.MAX_VALUE) {
/*  59 */       this.pos %= this.periods;
/*     */     }
/*  61 */     return calculateAve();
/*     */   }
/*     */   
/*     */ 
/*     */   public double[] getValues()
/*     */   {
/*  67 */     double[] res = new double[this.periods];
/*  68 */     int p = this.pos;
/*  69 */     for (int i = 0; i < this.periods; i++) {
/*  70 */       res[i] = this.data[(p++ % this.periods)];
/*     */     }
/*  72 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public double getAverage()
/*     */   {
/*  78 */     return calculateAve();
/*     */   }
/*     */   
/*  81 */   public int getPeriods() { return this.periods; }
/*     */   
/*     */   public int getSampleCount()
/*     */   {
/*  85 */     return this.pos > this.periods ? this.periods : this.pos;
/*     */   }
/*     */   
/*     */   private double calculateAve() {
/*  89 */     int lim = this.pos > this.periods ? this.periods : this.pos;
/*  90 */     if (lim == 0) {
/*  91 */       return 0.0D;
/*     */     }
/*  93 */     if (this.pos % this.periods == 0)
/*     */     {
/*  95 */       double sum = 0.0D;
/*  96 */       for (int i = 0; i < lim; i++) {
/*  97 */         sum += this.data[i];
/*     */       }
/*  99 */       this.total = sum;
/*     */     }
/* 101 */     return this.total / lim;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/average/MovingImmediateAverage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */