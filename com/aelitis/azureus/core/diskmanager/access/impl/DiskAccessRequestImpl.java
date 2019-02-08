/*     */ package com.aelitis.azureus.core.diskmanager.access.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.diskmanager.access.DiskAccessRequest;
/*     */ import com.aelitis.azureus.core.diskmanager.access.DiskAccessRequestListener;
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFile;
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileManagerException;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
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
/*     */ 
/*     */ 
/*     */ public class DiskAccessRequestImpl
/*     */   implements DiskAccessRequest
/*     */ {
/*     */   protected static final short OP_READ = 1;
/*     */   protected static final short OP_WRITE = 2;
/*     */   protected static final short OP_WRITE_AND_FREE = 3;
/*     */   private final CacheFile file;
/*     */   private final long offset;
/*     */   private final DirectByteBuffer buffer;
/*     */   private final DiskAccessRequestListener listener;
/*     */   private final short op;
/*     */   private final short cache_policy;
/*     */   private final int size;
/*     */   private volatile boolean cancelled;
/*     */   
/*     */   protected DiskAccessRequestImpl(CacheFile _file, long _offset, DirectByteBuffer _buffer, DiskAccessRequestListener _listener, short _op, short _cache_policy)
/*     */   {
/*  60 */     this.file = _file;
/*  61 */     this.offset = _offset;
/*  62 */     this.buffer = _buffer;
/*  63 */     this.listener = _listener;
/*  64 */     this.op = _op;
/*  65 */     this.cache_policy = _cache_policy;
/*     */     
/*  67 */     this.size = this.buffer.remaining((byte)4);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSize()
/*     */   {
/*  73 */     return this.size;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void runRequest()
/*     */   {
/*  79 */     if (this.cancelled)
/*     */     {
/*  81 */       this.listener.requestCancelled(this);
/*     */       
/*  83 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */     try
/*     */     {
/*  89 */       if (this.op == 1)
/*     */       {
/*  91 */         this.file.read(this.buffer, this.offset, this.cache_policy);
/*     */       }
/*  93 */       else if (this.op == 2)
/*     */       {
/*  95 */         this.file.write(this.buffer, this.offset);
/*     */       }
/*     */       else
/*     */       {
/*  99 */         this.file.writeAndHandoverBuffer(this.buffer, this.offset);
/*     */       }
/*     */       
/* 102 */       this.listener.requestExecuted(this.size);
/*     */       
/* 104 */       this.listener.requestComplete(this);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 108 */       this.listener.requestFailed(this, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean canBeAggregatedWith(DiskAccessRequestImpl other)
/*     */   {
/* 116 */     return (this.op == other.getOperation()) && (this.cache_policy == other.getCachePolicy());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static void runAggregated(DiskAccessRequestImpl base_request, DiskAccessRequestImpl[] requests)
/*     */   {
/* 126 */     int op = base_request.getOperation();
/*     */     
/* 128 */     CacheFile file = base_request.getFile();
/* 129 */     long offset = base_request.getOffset();
/* 130 */     short cache_policy = base_request.getCachePolicy();
/*     */     
/* 132 */     DirectByteBuffer[] buffers = new DirectByteBuffer[requests.length];
/*     */     
/* 134 */     long current_offset = offset;
/* 135 */     long total_size = 0L;
/*     */     
/* 137 */     for (int i = 0; i < buffers.length; i++)
/*     */     {
/* 139 */       DiskAccessRequestImpl request = requests[i];
/*     */       
/* 141 */       if (current_offset != request.getOffset())
/*     */       {
/* 143 */         Debug.out("assert failed: requests not contiguous");
/*     */       }
/*     */       
/* 146 */       int size = request.getSize();
/*     */       
/* 148 */       current_offset += size;
/*     */       
/* 150 */       total_size += size;
/*     */       
/* 152 */       buffers[i] = request.getBuffer();
/*     */     }
/*     */     try
/*     */     {
/* 156 */       if (op == 1)
/*     */       {
/* 158 */         file.read(buffers, offset, cache_policy);
/*     */       }
/* 160 */       else if (op == 2)
/*     */       {
/* 162 */         file.write(buffers, offset);
/*     */       }
/*     */       else
/*     */       {
/* 166 */         file.writeAndHandoverBuffers(buffers, offset);
/*     */       }
/*     */       
/* 169 */       base_request.getListener().requestExecuted(total_size);
/*     */       
/* 171 */       for (int i = 0; i < requests.length; i++)
/*     */       {
/* 173 */         DiskAccessRequestImpl request = requests[i];
/*     */         
/* 175 */         request.getListener().requestComplete(request);
/*     */         
/* 177 */         if (request != base_request)
/*     */         {
/* 179 */           request.getListener().requestExecuted(0L);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (CacheFileManagerException e)
/*     */     {
/* 185 */       int fail_index = e.getFailIndex();
/*     */       
/* 187 */       for (int i = 0; i < fail_index; i++)
/*     */       {
/* 189 */         DiskAccessRequestImpl request = requests[i];
/*     */         
/* 191 */         request.getListener().requestComplete(request);
/*     */       }
/*     */       
/* 194 */       for (int i = fail_index; i < requests.length; i++)
/*     */       {
/* 196 */         DiskAccessRequestImpl request = requests[i];
/*     */         
/* 198 */         request.getListener().requestFailed(request, e);
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 202 */       for (int i = 0; i < requests.length; i++)
/*     */       {
/* 204 */         DiskAccessRequestImpl request = requests[i];
/*     */         
/* 206 */         request.getListener().requestFailed(request, e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public CacheFile getFile()
/*     */   {
/* 214 */     return this.file;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getOffset()
/*     */   {
/* 220 */     return this.offset;
/*     */   }
/*     */   
/*     */ 
/*     */   public DirectByteBuffer getBuffer()
/*     */   {
/* 226 */     return this.buffer;
/*     */   }
/*     */   
/*     */ 
/*     */   public void cancel()
/*     */   {
/* 232 */     this.cancelled = true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isCancelled()
/*     */   {
/* 238 */     return this.cancelled;
/*     */   }
/*     */   
/*     */ 
/*     */   public short getCachePolicy()
/*     */   {
/* 244 */     return this.cache_policy;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getOperation()
/*     */   {
/* 250 */     return this.op;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPriority()
/*     */   {
/* 256 */     return this.listener.getPriority();
/*     */   }
/*     */   
/*     */ 
/*     */   protected DiskAccessRequestListener getListener()
/*     */   {
/* 262 */     return this.listener;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/access/impl/DiskAccessRequestImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */