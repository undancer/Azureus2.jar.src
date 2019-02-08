/*    */ package org.gudy.azureus2.core3.disk.impl.piecemapper.impl;
/*    */ 
/*    */ import org.gudy.azureus2.core3.disk.impl.DiskManagerFileInfoImpl;
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
/*    */ 
/*    */ 
/*    */ public class PieceMapEntryImpl
/*    */   implements DMPieceMapEntry
/*    */ {
/*    */   private final DiskManagerFileInfoImpl _file;
/*    */   private final long _offset;
/*    */   private final int _length;
/*    */   
/*    */   public PieceMapEntryImpl(DiskManagerFileInfoImpl file, long offset, int length)
/*    */   {
/* 45 */     this._file = file;
/* 46 */     this._offset = offset;
/* 47 */     this._length = length;
/*    */   }
/*    */   
/*    */   public DiskManagerFileInfoImpl getFile() {
/* 51 */     return this._file;
/*    */   }
/*    */   
/* 54 */   public long getOffset() { return this._offset; }
/*    */   
/*    */   public int getLength() {
/* 57 */     return this._length;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/piecemapper/impl/PieceMapEntryImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */