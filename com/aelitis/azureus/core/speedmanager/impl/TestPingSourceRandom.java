/*    */ package com.aelitis.azureus.core.speedmanager.impl;
/*    */ 
/*    */ import java.util.Random;
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
/*    */ public class TestPingSourceRandom
/*    */   extends TestPingSourceImpl
/*    */ {
/* 29 */   private final Random random = new Random();
/*    */   
/*    */ 
/*    */ 
/*    */   protected TestPingSourceRandom(SpeedManagerAlgorithmProviderAdapter adapter)
/*    */   {
/* 35 */     super(adapter);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   protected void updateSources(TestPingSourceImpl.testSource[] sources)
/*    */   {
/* 42 */     for (int i = 0; i < sources.length; i++)
/*    */     {
/* 44 */       sources[i].setRTT(this.random.nextInt(500));
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/TestPingSourceRandom.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */