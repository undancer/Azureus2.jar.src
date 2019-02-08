/*    */ package org.gudy.azureus2.core3.disk.impl.access.impl;
/*    */ 
/*    */ import org.gudy.azureus2.core3.disk.DiskManagerWriteRequest;
/*    */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
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
/*    */ public class DiskManagerWriteRequestImpl
/*    */   extends DiskManagerRequestImpl
/*    */   implements DiskManagerWriteRequest
/*    */ {
/*    */   private final int pieceNumber;
/*    */   private final int offset;
/*    */   private final DirectByteBuffer buffer;
/*    */   private final Object user_data;
/*    */   
/*    */   public DiskManagerWriteRequestImpl(int _pieceNumber, int _offset, DirectByteBuffer _buffer, Object _user_data)
/*    */   {
/* 43 */     this.pieceNumber = _pieceNumber;
/* 44 */     this.offset = _offset;
/* 45 */     this.buffer = _buffer;
/* 46 */     this.user_data = _user_data;
/*    */   }
/*    */   
/*    */ 
/*    */   protected String getName()
/*    */   {
/* 52 */     return "Write: " + this.pieceNumber + ",off=" + this.offset + ",len=" + this.buffer.remaining((byte)8);
/*    */   }
/*    */   
/*    */ 
/*    */   public int getPieceNumber()
/*    */   {
/* 58 */     return this.pieceNumber;
/*    */   }
/*    */   
/*    */ 
/*    */   public int getOffset()
/*    */   {
/* 64 */     return this.offset;
/*    */   }
/*    */   
/*    */ 
/*    */   public DirectByteBuffer getBuffer()
/*    */   {
/* 70 */     return this.buffer;
/*    */   }
/*    */   
/*    */ 
/*    */   public Object getUserData()
/*    */   {
/* 76 */     return this.user_data;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/access/impl/DiskManagerWriteRequestImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */