/*     */ package org.gudy.azureus2.pluginsimpl.local.utils;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.BDecoder;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBufferPool;
/*     */ import org.gudy.azureus2.plugins.utils.PooledByteBuffer;
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
/*     */ public class PooledByteBufferImpl
/*     */   implements PooledByteBuffer
/*     */ {
/*     */   private DirectByteBuffer buffer;
/*     */   
/*     */   public PooledByteBufferImpl(DirectByteBuffer _buffer)
/*     */   {
/*  41 */     this.buffer = _buffer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public PooledByteBufferImpl(int size)
/*     */   {
/*  48 */     this.buffer = DirectByteBufferPool.getBuffer((byte)1, size);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public PooledByteBufferImpl(byte[] data)
/*     */   {
/*  55 */     this.buffer = DirectByteBufferPool.getBuffer((byte)1, data.length);
/*     */     
/*  57 */     this.buffer.put((byte)1, data);
/*     */     
/*  59 */     this.buffer.position((byte)1, 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PooledByteBufferImpl(byte[] data, int offset, int length)
/*     */   {
/*  68 */     this.buffer = DirectByteBufferPool.getBuffer((byte)1, length);
/*     */     
/*  70 */     this.buffer.put((byte)1, data, offset, length);
/*     */     
/*  72 */     this.buffer.position((byte)1, 0);
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] toByteArray()
/*     */   {
/*  78 */     this.buffer.position((byte)1, 0);
/*     */     
/*  80 */     int len = this.buffer.limit((byte)1);
/*     */     
/*  82 */     byte[] res = new byte[len];
/*     */     
/*  84 */     this.buffer.get((byte)1, res);
/*     */     
/*  86 */     this.buffer.position((byte)1, 0);
/*     */     
/*  88 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public ByteBuffer toByteBuffer()
/*     */   {
/*  94 */     return this.buffer.getBuffer((byte)1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Map toMap()
/*     */     throws IOException
/*     */   {
/* 102 */     return BDecoder.decode(toByteArray());
/*     */   }
/*     */   
/*     */ 
/*     */   public DirectByteBuffer getBuffer()
/*     */   {
/* 108 */     return this.buffer;
/*     */   }
/*     */   
/*     */ 
/*     */   public void returnToPool()
/*     */   {
/* 114 */     this.buffer.returnToPool();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/PooledByteBufferImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */