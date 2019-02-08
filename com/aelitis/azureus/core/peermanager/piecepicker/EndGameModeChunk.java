/*    */ package com.aelitis.azureus.core.peermanager.piecepicker;
/*    */ 
/*    */ import org.gudy.azureus2.core3.peer.PEPiece;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class EndGameModeChunk
/*    */ {
/*    */   private final int pieceNumber;
/*    */   private final int blockNumber;
/*    */   private final int offset;
/*    */   private final int length;
/*    */   
/*    */   public EndGameModeChunk(PEPiece pePiece, int blockNum)
/*    */   {
/* 43 */     this.pieceNumber = pePiece.getPieceNumber();
/* 44 */     this.blockNumber = blockNum;
/* 45 */     this.length = pePiece.getBlockSize(this.blockNumber);
/* 46 */     this.offset = (this.blockNumber * 16384);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   /**
/*    */    * @deprecated
/*    */    */
/*    */   public boolean compare(int pieceNum, int os)
/*    */   {
/* 57 */     return (this.pieceNumber == pieceNum) && (this.offset == os);
/*    */   }
/*    */   
/*    */   public boolean equals(int pieceNum, int os)
/*    */   {
/* 62 */     return (this.pieceNumber == pieceNum) && (this.offset == os);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public int getPieceNumber()
/*    */   {
/* 70 */     return this.pieceNumber;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public int getBlockNumber()
/*    */   {
/* 78 */     return this.blockNumber;
/*    */   }
/*    */   
/*    */   public int getOffset()
/*    */   {
/* 83 */     return this.offset;
/*    */   }
/*    */   
/*    */   public int getLength()
/*    */   {
/* 88 */     return this.length;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/piecepicker/EndGameModeChunk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */