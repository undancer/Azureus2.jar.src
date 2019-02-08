/*    */ package com.aelitis.azureus.core.util.average;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MovingAverage
/*    */   implements Average
/*    */ {
/*    */   private final int periods;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private double[] data;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 28 */   private int pos = 0;
/*    */   
/*    */ 
/*    */   private double total;
/*    */   
/*    */ 
/*    */   public MovingAverage(int periods)
/*    */   {
/* 36 */     this.periods = periods;
/* 37 */     reset();
/*    */   }
/*    */   
/*    */   public void reset() {
/* 41 */     this.pos = 0;
/* 42 */     this.total = 0.0D;
/* 43 */     this.data = new double[this.periods];
/*    */   }
/*    */   
/*    */ 
/*    */   public double update(double newValue)
/*    */   {
/* 49 */     this.total -= this.data[this.pos];
/* 50 */     this.total += newValue;
/*    */     
/* 52 */     this.data[this.pos] = newValue;
/* 53 */     this.pos += 1;
/* 54 */     if (this.pos == this.periods) this.pos = 0;
/* 55 */     return calculateAve();
/*    */   }
/*    */   
/*    */ 
/*    */   public double getAverage()
/*    */   {
/* 61 */     return calculateAve();
/*    */   }
/*    */   
/*    */   private double calculateAve() {
/* 65 */     if (this.pos == 0)
/*    */     {
/* 67 */       double sum = 0.0D;
/* 68 */       for (int i = 0; i < this.periods; i++) {
/* 69 */         sum += this.data[i];
/*    */       }
/* 71 */       this.total = sum;
/*    */     }
/*    */     
/* 74 */     return this.total / this.periods;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/average/MovingAverage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */