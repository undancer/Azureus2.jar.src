/*    */ package org.gudy.azureus2.core3.util;
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
/*    */ public abstract class DirectByteBufferPool
/*    */ {
/*    */   static
/*    */   {
/* 33 */     if (System.getProperty("use.heap.buffers") != null)
/*    */     {
/*    */ 
/*    */ 
/* 37 */       Debug.outNoStack("******** USE_HEAP_BUFFERS MODE DEPRECATED ********"); }
/*    */   }
/*    */   
/* 40 */   private static final DirectByteBufferPool impl = new DirectByteBufferPoolReal();
/*    */   
/*    */ 
/*    */   protected abstract void returnBufferSupport(DirectByteBuffer paramDirectByteBuffer);
/*    */   
/*    */   protected abstract DirectByteBuffer getBufferSupport(byte paramByte, int paramInt);
/*    */   
/*    */   public static DirectByteBuffer getBuffer(byte allocator, int length)
/*    */   {
/* 49 */     return impl.getBufferSupport(allocator, length);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/DirectByteBufferPool.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */