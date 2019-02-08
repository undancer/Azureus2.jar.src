/*    */ package com.aelitis.azureus.core.neuronal;
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
/*    */ public class LogisticActivationFunction
/*    */   implements ActivationFunction
/*    */ {
/*    */   public double getDerivedFunctionValueFor(double x)
/*    */   {
/* 23 */     return x * (1.0D - x);
/*    */   }
/*    */   
/*    */   public double getValueFor(double x) {
/* 27 */     return 1.0D / (1.0D + Math.exp(-x));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/neuronal/LogisticActivationFunction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */