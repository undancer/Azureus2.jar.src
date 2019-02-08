/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.nio.ByteBuffer;
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
/*     */ public class ReferenceCountedDirectByteBuffer
/*     */   extends DirectByteBuffer
/*     */ {
/*     */   private DirectByteBuffer basis;
/*  31 */   private int ref_count = 1;
/*     */   
/*     */ 
/*     */ 
/*     */   protected ReferenceCountedDirectByteBuffer(DirectByteBuffer _basis)
/*     */   {
/*  37 */     this(_basis.getBufferInternal());
/*     */     
/*  39 */     this.basis = _basis;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected ReferenceCountedDirectByteBuffer(ByteBuffer _buffer)
/*     */   {
/*  46 */     super(_buffer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ReferenceCountedDirectByteBuffer duplicate(int offset, int length)
/*     */   {
/*  54 */     ByteBuffer duplicate = getBufferInternal().duplicate();
/*     */     
/*  56 */     duplicate.position(duplicate.position() + offset);
/*     */     
/*  58 */     duplicate.limit(duplicate.position() + length);
/*     */     
/*  60 */     ReferenceCountedDirectByteBuffer res = new ReferenceCountedDirectByteBufferDuplicate(duplicate);
/*     */     
/*  62 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public void incrementReferenceCount()
/*     */   {
/*  68 */     synchronized (this)
/*     */     {
/*  70 */       this.ref_count += 1;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void decrementReferenceCount()
/*     */   {
/*  79 */     synchronized (this)
/*     */     {
/*  81 */       this.ref_count -= 1;
/*     */       
/*     */ 
/*     */ 
/*  85 */       if (this.ref_count == 0)
/*     */       {
/*  87 */         this.basis.returnToPool();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public int getReferenceCount()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_1
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 53	org/gudy/azureus2/core3/util/ReferenceCountedDirectByteBuffer:ref_count	I
/*     */     //   8: aload_1
/*     */     //   9: monitorexit
/*     */     //   10: ireturn
/*     */     //   11: astore_2
/*     */     //   12: aload_1
/*     */     //   13: monitorexit
/*     */     //   14: aload_2
/*     */     //   15: athrow
/*     */     // Line number table:
/*     */     //   Java source line #95	-> byte code offset #0
/*     */     //   Java source line #97	-> byte code offset #4
/*     */     //   Java source line #98	-> byte code offset #11
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	16	0	this	ReferenceCountedDirectByteBuffer
/*     */     //   2	11	1	Ljava/lang/Object;	Object
/*     */     //   11	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	10	11	finally
/*     */     //   11	14	11	finally
/*     */   }
/*     */   
/*     */   public void returnToPool()
/*     */   {
/* 104 */     decrementReferenceCount();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected class ReferenceCountedDirectByteBufferDuplicate
/*     */     extends ReferenceCountedDirectByteBuffer
/*     */   {
/*     */     protected ReferenceCountedDirectByteBufferDuplicate(ByteBuffer owner)
/*     */     {
/* 115 */       super();
/*     */       
/* 117 */       incrementReferenceCount();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public ReferenceCountedDirectByteBuffer duplicate(int offset, int length)
/*     */     {
/* 125 */       Debug.out("dup dup");
/*     */       
/* 127 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */     public void returnToPool()
/*     */     {
/* 133 */       decrementReferenceCount();
/*     */     }
/*     */     
/*     */ 
/*     */     public void incrementReferenceCount()
/*     */     {
/* 139 */       ReferenceCountedDirectByteBuffer.this.incrementReferenceCount();
/*     */     }
/*     */     
/*     */ 
/*     */     public void decrementReferenceCount()
/*     */     {
/* 145 */       ReferenceCountedDirectByteBuffer.this.decrementReferenceCount();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/ReferenceCountedDirectByteBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */