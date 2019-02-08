/*    */ package org.gudy.azureus2.core3.peer.impl;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PEPieceWriteImpl
/*    */ {
/*    */   protected final int blockNumber;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected final String sender;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected final byte[] hash;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected final boolean correct;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public PEPieceWriteImpl(int blockNumber, String sender, byte[] hash, boolean correct)
/*    */   {
/* 40 */     this.blockNumber = blockNumber;
/* 41 */     this.sender = sender;
/* 42 */     this.hash = hash;
/* 43 */     this.correct = correct;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getSender()
/*    */   {
/* 49 */     return this.sender;
/*    */   }
/*    */   
/*    */ 
/*    */   public int getBlockNumber()
/*    */   {
/* 55 */     return this.blockNumber;
/*    */   }
/*    */   
/*    */ 
/*    */   public byte[] getHash()
/*    */   {
/* 61 */     return this.hash;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isCorrect()
/*    */   {
/* 67 */     return this.correct;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/impl/PEPieceWriteImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */