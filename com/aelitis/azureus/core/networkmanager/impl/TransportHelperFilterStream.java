/*     */ package com.aelitis.azureus.core.networkmanager.impl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBufferPool;
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
/*     */ public abstract class TransportHelperFilterStream
/*     */   implements TransportHelperFilter
/*     */ {
/*     */   private final TransportHelper transport;
/*     */   private DirectByteBuffer write_buffer_pending_db;
/*     */   private ByteBuffer write_buffer_pending_byte;
/*     */   
/*     */   protected TransportHelperFilterStream(TransportHelper _transport)
/*     */   {
/*  43 */     this.transport = _transport;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean hasBufferedWrite()
/*     */   {
/*  49 */     return (this.write_buffer_pending_db != null) || (this.write_buffer_pending_byte != null) || (this.transport.hasDelayedWrite());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasBufferedRead()
/*     */   {
/*  57 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public TransportHelper getHelper()
/*     */   {
/*  63 */     return this.transport;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long write(ByteBuffer[] buffers, int array_offset, int length)
/*     */     throws IOException
/*     */   {
/*  76 */     if (this.write_buffer_pending_byte != null)
/*     */     {
/*  78 */       if (this.transport.write(this.write_buffer_pending_byte, false) == 0)
/*     */       {
/*  80 */         return 0L;
/*     */       }
/*     */       
/*  83 */       this.write_buffer_pending_byte = null;
/*     */     }
/*     */     
/*  86 */     long total_written = 0L;
/*     */     
/*  88 */     if (this.write_buffer_pending_db != null)
/*     */     {
/*  90 */       ByteBuffer write_buffer_pending = this.write_buffer_pending_db.getBuffer((byte)5);
/*     */       
/*  92 */       int max_writable = 0;
/*     */       
/*  94 */       for (int i = array_offset; i < array_offset + length; i++)
/*     */       {
/*  96 */         ByteBuffer source_buffer = buffers[i];
/*     */         
/*  98 */         int position = source_buffer.position();
/*  99 */         int limit = source_buffer.limit();
/*     */         
/* 101 */         int size = limit - position;
/*     */         
/* 103 */         max_writable += size;
/*     */       }
/*     */       
/* 106 */       int pending_position = write_buffer_pending.position();
/* 107 */       int pending_limit = write_buffer_pending.limit();
/*     */       
/* 109 */       int pending_writable = pending_limit - pending_position;
/*     */       
/* 111 */       if (pending_writable > max_writable)
/*     */       {
/* 113 */         pending_writable = max_writable;
/*     */         
/* 115 */         write_buffer_pending.limit(pending_position + pending_writable);
/*     */       }
/*     */       
/* 118 */       int written = this.transport.write(write_buffer_pending, false);
/*     */       
/* 120 */       write_buffer_pending.limit(pending_limit);
/*     */       
/* 122 */       if (written > 0)
/*     */       {
/* 124 */         total_written = written;
/*     */         
/* 126 */         if (write_buffer_pending.remaining() == 0)
/*     */         {
/* 128 */           this.write_buffer_pending_db.returnToPool();
/*     */           
/* 130 */           this.write_buffer_pending_db = null;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 135 */         int skip = written;
/*     */         
/* 137 */         for (int i = array_offset; i < array_offset + length; i++)
/*     */         {
/* 139 */           ByteBuffer source_buffer = buffers[i];
/*     */           
/* 141 */           int position = source_buffer.position();
/* 142 */           int limit = source_buffer.limit();
/*     */           
/* 144 */           int size = limit - position;
/*     */           
/* 146 */           if (size <= skip)
/*     */           {
/* 148 */             source_buffer.position(limit);
/*     */             
/* 150 */             skip -= size;
/*     */           }
/*     */           else
/*     */           {
/* 154 */             source_buffer.position(position + skip);
/*     */             
/* 156 */             skip = 0;
/*     */             
/* 158 */             break;
/*     */           }
/*     */         }
/*     */         
/* 162 */         if (skip != 0)
/*     */         {
/* 164 */           throw new IOException("skip inconsistent - " + skip);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 171 */       if ((total_written < pending_writable) || (total_written == max_writable))
/*     */       {
/* 173 */         return total_written;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 181 */     for (int i = array_offset; i < array_offset + length; i++)
/*     */     {
/* 183 */       ByteBuffer source_buffer = buffers[i];
/*     */       
/* 185 */       int position = source_buffer.position();
/* 186 */       int limit = source_buffer.limit();
/*     */       
/* 188 */       int size = limit - position;
/*     */       
/* 190 */       if (size != 0)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 195 */         DirectByteBuffer target_buffer_db = DirectByteBufferPool.getBuffer((byte)26, size);
/*     */         try
/*     */         {
/* 198 */           ByteBuffer target_buffer = target_buffer_db.getBuffer((byte)5);
/*     */           
/* 200 */           cryptoOut(source_buffer, target_buffer);
/*     */           
/* 202 */           target_buffer.position(0);
/*     */           
/* 204 */           boolean partial_write = false;
/*     */           
/* 206 */           for (int j = i + 1; j < array_offset + length; j++)
/*     */           {
/* 208 */             if (buffers[j].hasRemaining())
/*     */             {
/* 210 */               partial_write = true;
/*     */             }
/*     */           }
/*     */           
/* 214 */           int written = this.transport.write(target_buffer, partial_write);
/*     */           
/* 216 */           total_written += written;
/*     */           
/* 218 */           source_buffer.position(position + written);
/*     */           
/* 220 */           if (written < size)
/*     */           {
/* 222 */             this.write_buffer_pending_db = target_buffer_db;
/*     */             
/* 224 */             target_buffer_db = null;
/*     */             
/* 226 */             if (written == 0)
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/* 231 */               this.write_buffer_pending_byte = ByteBuffer.wrap(new byte[] { target_buffer.get() });
/*     */               
/* 233 */               source_buffer.get();
/*     */               
/* 235 */               total_written += 1L;
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 242 */             if (target_buffer_db == null)
/*     */               break;
/* 244 */             target_buffer_db.returnToPool(); break;
/*     */           }
/*     */         }
/*     */         finally
/*     */         {
/* 242 */           if (target_buffer_db != null)
/*     */           {
/* 244 */             target_buffer_db.returnToPool();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 251 */     return total_written;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long read(ByteBuffer[] buffers, int array_offset, int length)
/*     */     throws IOException
/*     */   {
/* 262 */     int total_read = 0;
/*     */     
/* 264 */     DirectByteBuffer[] copy_db = new DirectByteBuffer[buffers.length];
/* 265 */     ByteBuffer[] copy = new ByteBuffer[buffers.length];
/*     */     try
/*     */     {
/* 268 */       for (int i = array_offset; i < array_offset + length; i++)
/*     */       {
/* 270 */         ByteBuffer buffer = buffers[i];
/*     */         
/* 272 */         int size = buffer.remaining();
/*     */         
/* 274 */         if (size > 0)
/*     */         {
/* 276 */           copy_db[i] = DirectByteBufferPool.getBuffer(26, size);
/*     */           
/* 278 */           copy[i] = copy_db[i].getBuffer(5);
/*     */         }
/*     */         else {
/* 281 */           copy[i] = ByteBuffer.allocate(0);
/*     */         }
/*     */       }
/*     */       
/* 285 */       total_read = (int)(total_read + this.transport.read(copy, array_offset, length));
/*     */       
/* 287 */       for (int i = array_offset; i < array_offset + length; i++)
/*     */       {
/* 289 */         ByteBuffer source_buffer = copy[i];
/*     */         
/* 291 */         if (source_buffer != null)
/*     */         {
/* 293 */           ByteBuffer target_buffer = buffers[i];
/*     */           
/* 295 */           int source_position = source_buffer.position();
/*     */           
/* 297 */           if (source_position > 0)
/*     */           {
/* 299 */             source_buffer.flip();
/*     */             
/* 301 */             cryptoIn(source_buffer, target_buffer);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */       int i;
/*     */       
/* 308 */       return total_read;
/*     */     }
/*     */     finally
/*     */     {
/* 312 */       for (int i = 0; i < copy_db.length; i++)
/*     */       {
/* 314 */         if (copy_db[i] != null)
/*     */         {
/* 316 */           copy_db[i].returnToPool();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setTrace(boolean on)
/*     */   {
/* 326 */     this.transport.setTrace(on);
/*     */   }
/*     */   
/*     */   protected abstract void cryptoOut(ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2)
/*     */     throws IOException;
/*     */   
/*     */   protected abstract void cryptoIn(ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2)
/*     */     throws IOException;
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/TransportHelperFilterStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */