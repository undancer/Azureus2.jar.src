/*    */ package com.aelitis.azureus.core.util.average;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RunningAverage
/*    */   implements Average
/*    */ {
/* 26 */   private long count = 0L;
/*    */   
/*    */   private double sum;
/*    */   
/*    */ 
/*    */   public RunningAverage()
/*    */   {
/* 33 */     this.sum = 0.0D;
/*    */   }
/*    */   
/*    */   public void reset() {
/* 37 */     this.count = 0L;
/* 38 */     this.sum = 0.0D;
/*    */   }
/*    */   
/*    */ 
/*    */   public double update(double newValue)
/*    */   {
/* 44 */     this.sum += newValue;
/* 45 */     this.count += 1L;
/* 46 */     return this.sum / this.count;
/*    */   }
/*    */   
/*    */ 
/*    */   public double getAverage()
/*    */   {
/* 52 */     return this.count == 0L ? 0.0D : this.sum / this.count;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/average/RunningAverage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */