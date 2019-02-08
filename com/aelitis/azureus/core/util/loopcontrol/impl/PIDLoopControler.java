/*    */ package com.aelitis.azureus.core.util.loopcontrol.impl;
/*    */ 
/*    */ import com.aelitis.azureus.core.util.loopcontrol.LoopControler;
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
/*    */ public class PIDLoopControler
/*    */   implements LoopControler
/*    */ {
/*    */   final double pGain;
/*    */   final double iGain;
/*    */   final double dGain;
/*    */   double iState;
/*    */   static final double iMin = -5000.0D;
/*    */   static final double iMax = 5000.0D;
/*    */   double dState;
/*    */   
/*    */   public PIDLoopControler(double pGain, double iGain, double dGain)
/*    */   {
/* 35 */     this.pGain = pGain;
/* 36 */     this.iGain = iGain;
/* 37 */     this.dGain = dGain;
/*    */   }
/*    */   
/*    */ 
/*    */   public double updateControler(double error, double position)
/*    */   {
/* 43 */     double pTerm = this.pGain * error;
/*    */     
/*    */ 
/* 46 */     this.iState += error;
/* 47 */     if (this.iState > 5000.0D) this.iState = 5000.0D;
/* 48 */     if (this.iState < -5000.0D) { this.iState = -5000.0D;
/*    */     }
/* 50 */     double iTerm = this.iGain * this.iState;
/*    */     
/* 52 */     double d = this.dState - position;
/*    */     
/* 54 */     double dTerm = this.dGain * d;
/* 55 */     this.dState = position;
/*    */     
/* 57 */     double result = pTerm + iTerm - dTerm;
/*    */     
/* 59 */     System.out.println("PID p,i,d (" + this.pGain + "," + this.iGain + "," + this.dGain + ") : is,ds (" + this.iState + "," + d + ") p,i,d (" + pTerm + "," + iTerm + "," + dTerm + ") => " + result);
/*    */     
/* 61 */     return result;
/*    */   }
/*    */   
/*    */   public void reset() {
/* 65 */     this.dState = 0.0D;
/* 66 */     this.iState = 0.0D;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/loopcontrol/impl/PIDLoopControler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */