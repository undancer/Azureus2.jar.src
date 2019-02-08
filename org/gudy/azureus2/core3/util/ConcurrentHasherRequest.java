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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConcurrentHasherRequest
/*     */ {
/*  32 */   private static final AEMonitor class_mon = new AEMonitor("ConcHashRequest:class");
/*     */   
/*     */   private final ConcurrentHasher concurrent_hasher;
/*     */   
/*     */   private final ByteBuffer buffer;
/*     */   
/*     */   private ConcurrentHasherRequestListener listener;
/*     */   private final int size;
/*     */   private byte[] result;
/*     */   private boolean cancelled;
/*     */   private final boolean low_priority;
/*  43 */   private final AESemaphore sem = new AESemaphore("ConcHashRequest");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ConcurrentHasherRequest(ConcurrentHasher _concurrent_hasher, ByteBuffer _buffer, ConcurrentHasherRequestListener _listener, boolean _low_priorty)
/*     */   {
/*  52 */     this.concurrent_hasher = _concurrent_hasher;
/*  53 */     this.buffer = _buffer;
/*  54 */     this.listener = _listener;
/*  55 */     this.low_priority = _low_priorty;
/*     */     
/*  57 */     this.size = (this.buffer.limit() - this.buffer.position());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getResult()
/*     */   {
/*  68 */     this.sem.reserve();
/*     */     
/*  70 */     return this.result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void cancel()
/*     */   {
/*  81 */     if (!this.cancelled)
/*     */     {
/*  83 */       this.cancelled = true;
/*     */       
/*  85 */       this.sem.releaseForever();
/*     */       
/*     */       ConcurrentHasherRequestListener listener_copy;
/*     */       try
/*     */       {
/*  90 */         class_mon.enter();
/*     */         
/*  92 */         listener_copy = this.listener;
/*     */         
/*  94 */         this.listener = null;
/*     */       }
/*     */       finally
/*     */       {
/*  98 */         class_mon.exit();
/*     */       }
/*     */       
/* 101 */       if (listener_copy != null)
/*     */       {
/* 103 */         listener_copy.complete(this);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getCancelled()
/*     */   {
/* 111 */     return this.cancelled;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSize()
/*     */   {
/* 117 */     return this.size;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isLowPriority()
/*     */   {
/* 123 */     return this.low_priority;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void run(SHA1Hasher hasher)
/*     */   {
/* 130 */     if (!this.cancelled)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 138 */       this.result = hasher.calculateHash(this.buffer);
/*     */       
/*     */ 
/* 141 */       this.sem.releaseForever();
/*     */       
/* 143 */       if (!this.cancelled)
/*     */       {
/*     */         ConcurrentHasherRequestListener listener_copy;
/*     */         try
/*     */         {
/* 148 */           class_mon.enter();
/*     */           
/* 150 */           listener_copy = this.listener;
/*     */           
/* 152 */           this.listener = null;
/*     */         }
/*     */         finally
/*     */         {
/* 156 */           class_mon.exit();
/*     */         }
/*     */         
/* 159 */         if (listener_copy != null)
/*     */         {
/* 161 */           listener_copy.complete(this);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/ConcurrentHasherRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */