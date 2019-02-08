/*    */ package com.aelitis.azureus.core.util.average;
/*    */ 
/*    */ import java.io.PrintStream;
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
/*    */ 
/*    */ public class ExponentialMovingAverage
/*    */   implements Average
/*    */ {
/*    */   private final float weight;
/*    */   private double prevEMA;
/*    */   
/*    */   public ExponentialMovingAverage(int periods)
/*    */   {
/* 34 */     if (periods < 1) {
/* 35 */       System.out.println("ExponentialMovingAverage:: ERROR: bad periods: " + periods);
/*    */     }
/* 37 */     this.weight = (2.0F / (1 + periods));
/* 38 */     this.prevEMA = 0.0D;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public ExponentialMovingAverage(float weight)
/*    */   {
/* 46 */     if ((weight < 0.0D) || (weight > 1.0D)) {
/* 47 */       System.out.println("ExponentialMovingAverage:: ERROR: bad weight: " + weight);
/*    */     }
/* 49 */     this.weight = weight;
/* 50 */     this.prevEMA = 0.0D;
/*    */   }
/*    */   
/*    */   public void reset()
/*    */   {
/* 55 */     this.prevEMA = 0.0D;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public double update(double newValue)
/*    */   {
/* 62 */     this.prevEMA = (this.weight * (newValue - this.prevEMA) + this.prevEMA);
/* 63 */     return this.prevEMA;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public double getAverage()
/*    */   {
/* 70 */     return this.prevEMA;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/average/ExponentialMovingAverage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */