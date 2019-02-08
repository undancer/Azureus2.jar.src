/*    */ package org.gudy.azureus2.core3.disk.impl.piecemapper.impl;
/*    */ 
/*    */ import java.util.List;
/*    */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceList;
/*    */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceMapEntry;
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
/*    */ public class PieceListImpl
/*    */   implements DMPieceList
/*    */ {
/*    */   private final PieceMapEntryImpl[] pieces;
/*    */   private final int[] cumulativeLengths;
/*    */   
/*    */   public static PieceListImpl convert(List pieceList)
/*    */   {
/* 43 */     return new PieceListImpl((PieceMapEntryImpl[])pieceList.toArray(new PieceMapEntryImpl[pieceList.size()]));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   protected PieceListImpl(PieceMapEntryImpl[] _pieces)
/*    */   {
/* 50 */     this.pieces = _pieces;
/* 51 */     this.cumulativeLengths = new int[this.pieces.length];
/*    */     
/* 53 */     initializeCumulativeLengths();
/*    */   }
/*    */   
/*    */ 
/*    */   private void initializeCumulativeLengths()
/*    */   {
/* 59 */     int runningLength = 0;
/* 60 */     for (int i = 0; i < this.pieces.length; i++) {
/* 61 */       runningLength += this.pieces[i].getLength();
/* 62 */       this.cumulativeLengths[i] = runningLength;
/*    */     }
/*    */   }
/*    */   
/*    */   public int size() {
/* 67 */     return this.pieces.length;
/*    */   }
/*    */   
/*    */   public DMPieceMapEntry get(int index) {
/* 71 */     return this.pieces[index];
/*    */   }
/*    */   
/*    */   public int getCumulativeLengthToPiece(int index) {
/* 75 */     return this.cumulativeLengths[index];
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/piecemapper/impl/PieceListImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */