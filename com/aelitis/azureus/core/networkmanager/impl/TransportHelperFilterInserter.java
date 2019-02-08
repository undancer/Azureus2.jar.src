/*     */ package com.aelitis.azureus.core.networkmanager.impl;
/*     */ 
/*     */ import java.io.IOException;
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
/*     */ public class TransportHelperFilterInserter
/*     */   implements TransportHelperFilter
/*     */ {
/*     */   private final TransportHelperFilter target_filter;
/*     */   private ByteBuffer read_insert;
/*     */   
/*     */   public TransportHelperFilterInserter(TransportHelperFilter _target_filter, ByteBuffer _read_insert)
/*     */   {
/*  38 */     this.target_filter = _target_filter;
/*     */     
/*  40 */     this.read_insert = _read_insert;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long write(ByteBuffer[] buffers, int array_offset, int length)
/*     */     throws IOException
/*     */   {
/*  51 */     return this.target_filter.write(buffers, array_offset, length);
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
/*  62 */     long total_read = 0L;
/*     */     
/*  64 */     if (this.read_insert != null)
/*     */     {
/*  66 */       int pos_before = this.read_insert.position();
/*     */       
/*  68 */       for (int i = array_offset; i < array_offset + length; i++)
/*     */       {
/*  70 */         ByteBuffer buffer = buffers[i];
/*     */         
/*  72 */         int space = buffer.remaining();
/*     */         
/*  74 */         if (space > 0)
/*     */         {
/*  76 */           if (space < this.read_insert.remaining())
/*     */           {
/*  78 */             int old_limit = this.read_insert.limit();
/*     */             
/*  80 */             this.read_insert.limit(this.read_insert.position() + space);
/*     */             
/*  82 */             buffer.put(this.read_insert);
/*     */             
/*  84 */             this.read_insert.limit(old_limit);
/*     */           }
/*     */           else
/*     */           {
/*  88 */             buffer.put(this.read_insert);
/*     */           }
/*     */           
/*  91 */           if (!this.read_insert.hasRemaining()) {
/*     */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*  98 */       total_read = this.read_insert.position() - pos_before;
/*     */       
/* 100 */       if (this.read_insert.hasRemaining())
/*     */       {
/* 102 */         return total_read;
/*     */       }
/*     */       
/*     */ 
/* 106 */       this.read_insert = null;
/*     */     }
/*     */     
/*     */ 
/* 110 */     total_read += this.target_filter.read(buffers, array_offset, length);
/*     */     
/* 112 */     return total_read;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean hasBufferedWrite()
/*     */   {
/* 118 */     return this.target_filter.hasBufferedWrite();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean hasBufferedRead()
/*     */   {
/* 124 */     return (this.read_insert != null) || (this.target_filter.hasBufferedRead());
/*     */   }
/*     */   
/*     */ 
/*     */   public TransportHelper getHelper()
/*     */   {
/* 130 */     return this.target_filter.getHelper();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setTrace(boolean on)
/*     */   {
/* 137 */     this.target_filter.setTrace(on);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isEncrypted()
/*     */   {
/* 143 */     return this.target_filter.isEncrypted();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName(boolean verbose)
/*     */   {
/* 149 */     return this.target_filter.getName(verbose);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/TransportHelperFilterInserter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */