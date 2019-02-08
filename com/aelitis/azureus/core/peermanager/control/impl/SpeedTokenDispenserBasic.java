/*    */ package com.aelitis.azureus.core.peermanager.control.impl;
/*    */ 
/*    */ import com.aelitis.azureus.core.peermanager.control.SpeedTokenDispenser;
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
/*    */ public class SpeedTokenDispenserBasic
/*    */   implements SpeedTokenDispenser
/*    */ {
/*    */   public int dispense(int numberOfChunks, int chunkSize)
/*    */   {
/* 31 */     return numberOfChunks;
/*    */   }
/*    */   
/*    */ 
/*    */   public void returnUnusedChunks(int unused, int chunkSize) {}
/*    */   
/*    */ 
/*    */   public int peek(int chunkSize)
/*    */   {
/* 40 */     return chunkSize;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/control/impl/SpeedTokenDispenserBasic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */