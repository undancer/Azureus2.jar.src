/*     */ package org.gudy.azureus2.core3.disk.impl.piecemapper.impl;
/*     */ 
/*     */ import org.gudy.azureus2.core3.disk.impl.DiskManagerFileInfoImpl;
/*     */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceList;
/*     */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceMap;
/*     */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceMapEntry;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DMPieceMapSimple
/*     */   implements DMPieceMap
/*     */ {
/*     */   private final int piece_length;
/*     */   private final int piece_count;
/*     */   private final int last_piece_length;
/*     */   private final DiskManagerFileInfoImpl file;
/*     */   
/*     */   protected DMPieceMapSimple(TOTorrent torrent, DiskManagerFileInfoImpl _file)
/*     */   {
/*  44 */     this.piece_length = ((int)torrent.getPieceLength());
/*     */     
/*  46 */     this.piece_count = torrent.getNumberOfPieces();
/*     */     
/*  48 */     int lpl = (int)(torrent.getSize() % this.piece_length);
/*     */     
/*  50 */     if (lpl == 0)
/*     */     {
/*  52 */       lpl = this.piece_length;
/*     */     }
/*     */     
/*  55 */     this.last_piece_length = lpl;
/*     */     
/*  57 */     this.file = _file;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DMPieceList getPieceList(int piece_number)
/*     */   {
/*  64 */     return new pieceList(piece_number);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected class pieceList
/*     */     implements DMPieceList, DMPieceMapEntry
/*     */   {
/*     */     private final int piece_number;
/*     */     
/*     */ 
/*     */     protected pieceList(int _piece_number)
/*     */     {
/*  77 */       this.piece_number = _piece_number;
/*     */     }
/*     */     
/*     */ 
/*     */     public int size()
/*     */     {
/*  83 */       return 1;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public DMPieceMapEntry get(int index)
/*     */     {
/*  90 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int getCumulativeLengthToPiece(int file_index)
/*     */     {
/*  97 */       return getLength();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public DiskManagerFileInfoImpl getFile()
/*     */     {
/* 105 */       return DMPieceMapSimple.this.file;
/*     */     }
/*     */     
/*     */ 
/*     */     public long getOffset()
/*     */     {
/* 111 */       return this.piece_number * DMPieceMapSimple.this.piece_length;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getLength()
/*     */     {
/* 117 */       if (this.piece_number == DMPieceMapSimple.this.piece_count - 1)
/*     */       {
/* 119 */         return DMPieceMapSimple.this.last_piece_length;
/*     */       }
/*     */       
/* 122 */       return DMPieceMapSimple.this.piece_length;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/piecemapper/impl/DMPieceMapSimple.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */