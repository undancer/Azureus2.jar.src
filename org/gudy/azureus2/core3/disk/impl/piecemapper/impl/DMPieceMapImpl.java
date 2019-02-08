/*    */ package org.gudy.azureus2.core3.disk.impl.piecemapper.impl;
/*    */ 
/*    */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceList;
/*    */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceMap;
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
/*    */ public class DMPieceMapImpl
/*    */   implements DMPieceMap
/*    */ {
/*    */   private final DMPieceList[] piece_lists;
/*    */   
/*    */   protected DMPieceMapImpl(DMPieceList[] _piece_lists)
/*    */   {
/* 36 */     this.piece_lists = _piece_lists;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public DMPieceList getPieceList(int piece_number)
/*    */   {
/* 43 */     return this.piece_lists[piece_number];
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/piecemapper/impl/DMPieceMapImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */