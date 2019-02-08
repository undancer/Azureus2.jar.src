/*     */ package org.gudy.azureus2.pluginsimpl.local.disk;
/*     */ 
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerException;
/*     */ import org.gudy.azureus2.plugins.utils.PooledByteBuffer;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.PooledByteBufferImpl;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DiskManagerImpl
/*     */   implements org.gudy.azureus2.plugins.disk.DiskManager
/*     */ {
/*     */   private org.gudy.azureus2.core3.disk.DiskManager disk_manager;
/*     */   
/*     */   public DiskManagerImpl(org.gudy.azureus2.core3.disk.DiskManager _disk_manager)
/*     */   {
/*  47 */     this.disk_manager = _disk_manager;
/*     */   }
/*     */   
/*     */ 
/*     */   public org.gudy.azureus2.core3.disk.DiskManager getDiskmanager()
/*     */   {
/*  53 */     return this.disk_manager;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public org.gudy.azureus2.plugins.disk.DiskManagerReadRequest read(int piece_number, int offset, int length, final org.gudy.azureus2.plugins.disk.DiskManagerReadRequestListener listener)
/*     */     throws DiskManagerException
/*     */   {
/*  65 */     if (!this.disk_manager.checkBlockConsistencyForRead("plugin", false, piece_number, offset, length))
/*     */     {
/*  67 */       throw new DiskManagerException("read invalid - parameters incorrect or piece incomplete");
/*     */     }
/*     */     
/*  70 */     final DMRR request = new DMRR(this.disk_manager.createReadRequest(piece_number, offset, length), null);
/*     */     
/*  72 */     this.disk_manager.enqueueReadRequest(request.getDelegate(), new org.gudy.azureus2.core3.disk.DiskManagerReadRequestListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void readCompleted(org.gudy.azureus2.core3.disk.DiskManagerReadRequest _request, DirectByteBuffer _data)
/*     */       {
/*     */ 
/*     */ 
/*  81 */         listener.complete(request, new PooledByteBufferImpl(_data));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void readFailed(org.gudy.azureus2.core3.disk.DiskManagerReadRequest _request, Throwable _cause)
/*     */       {
/*  89 */         listener.failed(request, new DiskManagerException("read failed", _cause));
/*     */       }
/*     */       
/*     */ 
/*     */       public int getPriority()
/*     */       {
/*  95 */         return 0;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void requestExecuted(long bytes) {}
/* 104 */     });
/* 105 */     return request;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public org.gudy.azureus2.plugins.disk.DiskManagerWriteRequest write(final int piece_number, final int offset, PooledByteBuffer data, final org.gudy.azureus2.plugins.disk.DiskManagerWriteRequestListener listener)
/*     */     throws DiskManagerException
/*     */   {
/* 117 */     DirectByteBuffer buffer = ((PooledByteBufferImpl)data).getBuffer();
/*     */     
/* 119 */     if (!this.disk_manager.checkBlockConsistencyForWrite("plugin", piece_number, offset, buffer))
/*     */     {
/* 121 */       throw new DiskManagerException("write invalid - parameters incorrect");
/*     */     }
/*     */     
/* 124 */     final int length = buffer.remaining((byte)1);
/*     */     
/* 126 */     final DMWR request = new DMWR(this.disk_manager.createWriteRequest(piece_number, offset, buffer, null), length, null);
/*     */     
/* 128 */     this.disk_manager.enqueueWriteRequest(request.getDelegate(), new org.gudy.azureus2.core3.disk.DiskManagerWriteRequestListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void writeCompleted(org.gudy.azureus2.core3.disk.DiskManagerWriteRequest _request)
/*     */       {
/*     */ 
/*     */ 
/* 136 */         DiskManagerPiece[] dm_pieces = DiskManagerImpl.this.disk_manager.getPieces();
/*     */         
/* 138 */         DiskManagerPiece dm_piece = dm_pieces[piece_number];
/*     */         
/* 140 */         if (!dm_piece.isDone())
/*     */         {
/* 142 */           int current_offset = offset;
/*     */           
/* 144 */           for (int i = 0; i < length; i += 16384)
/*     */           {
/* 146 */             dm_piece.setWritten(current_offset / 16384);
/*     */             
/* 148 */             current_offset += 16384;
/*     */           }
/*     */         }
/*     */         
/* 152 */         listener.complete(request);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void writeFailed(org.gudy.azureus2.core3.disk.DiskManagerWriteRequest _request, Throwable _cause)
/*     */       {
/* 160 */         listener.failed(request, new DiskManagerException("read failed", _cause));
/*     */       }
/*     */       
/* 163 */     });
/* 164 */     return request;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class DMRR
/*     */     implements org.gudy.azureus2.plugins.disk.DiskManagerReadRequest
/*     */   {
/*     */     private org.gudy.azureus2.core3.disk.DiskManagerReadRequest request;
/*     */     
/*     */ 
/*     */     private DMRR(org.gudy.azureus2.core3.disk.DiskManagerReadRequest _request)
/*     */     {
/* 177 */       this.request = _request;
/*     */     }
/*     */     
/*     */ 
/*     */     private org.gudy.azureus2.core3.disk.DiskManagerReadRequest getDelegate()
/*     */     {
/* 183 */       return this.request;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getPieceNumber()
/*     */     {
/* 189 */       return this.request.getPieceNumber();
/*     */     }
/*     */     
/*     */ 
/*     */     public int getOffset()
/*     */     {
/* 195 */       return this.request.getOffset();
/*     */     }
/*     */     
/*     */ 
/*     */     public int getLength()
/*     */     {
/* 201 */       return this.request.getLength();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class DMWR
/*     */     implements org.gudy.azureus2.plugins.disk.DiskManagerWriteRequest
/*     */   {
/*     */     private org.gudy.azureus2.core3.disk.DiskManagerWriteRequest request;
/*     */     
/*     */     private int length;
/*     */     
/*     */ 
/*     */     private DMWR(org.gudy.azureus2.core3.disk.DiskManagerWriteRequest _request, int _length)
/*     */     {
/* 217 */       this.request = _request;
/*     */     }
/*     */     
/*     */ 
/*     */     private org.gudy.azureus2.core3.disk.DiskManagerWriteRequest getDelegate()
/*     */     {
/* 223 */       return this.request;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getPieceNumber()
/*     */     {
/* 229 */       return this.request.getPieceNumber();
/*     */     }
/*     */     
/*     */ 
/*     */     public int getOffset()
/*     */     {
/* 235 */       return this.request.getOffset();
/*     */     }
/*     */     
/*     */ 
/*     */     public int getLength()
/*     */     {
/* 241 */       return this.length;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/disk/DiskManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */