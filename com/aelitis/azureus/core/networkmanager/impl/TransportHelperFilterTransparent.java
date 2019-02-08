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
/*     */ 
/*     */ public class TransportHelperFilterTransparent
/*     */   implements TransportHelperFilter
/*     */ {
/*     */   private final TransportHelper transport;
/*     */   private final boolean is_plain;
/*     */   private ByteBuffer read_insert;
/*     */   
/*     */   public TransportHelperFilterTransparent(TransportHelper _transport, boolean _is_plain)
/*     */   {
/*  40 */     this.transport = _transport;
/*  41 */     this.is_plain = _is_plain;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void insertRead(ByteBuffer _read_insert)
/*     */   {
/*  48 */     this.read_insert = _read_insert;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean hasBufferedWrite()
/*     */   {
/*  54 */     return this.transport.hasDelayedWrite();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean hasBufferedRead()
/*     */   {
/*  60 */     return (this.read_insert != null) && (this.read_insert.remaining() > 0);
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
/*  71 */     return this.transport.write(buffers, array_offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int write(ByteBuffer buffer, boolean partial_write)
/*     */     throws IOException
/*     */   {
/*  81 */     return this.transport.write(buffer, partial_write);
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
/*  92 */     int len = 0;
/*     */     
/*  94 */     if (this.read_insert != null)
/*     */     {
/*  96 */       int pos_before = this.read_insert.position();
/*     */       
/*  98 */       for (int i = array_offset; i < array_offset + length; i++)
/*     */       {
/* 100 */         ByteBuffer buffer = buffers[i];
/*     */         
/* 102 */         int space = buffer.remaining();
/*     */         
/* 104 */         if (space > 0)
/*     */         {
/* 106 */           if (space < this.read_insert.remaining())
/*     */           {
/* 108 */             int old_limit = this.read_insert.limit();
/*     */             
/* 110 */             this.read_insert.limit(this.read_insert.position() + space);
/*     */             
/* 112 */             buffer.put(this.read_insert);
/*     */             
/* 114 */             this.read_insert.limit(old_limit);
/*     */           }
/*     */           else
/*     */           {
/* 118 */             buffer.put(this.read_insert);
/*     */           }
/*     */           
/* 121 */           if (!this.read_insert.hasRemaining()) {
/*     */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 128 */       len = this.read_insert.position() - pos_before;
/*     */       
/* 130 */       if (this.read_insert.hasRemaining())
/*     */       {
/* 132 */         return len;
/*     */       }
/*     */       
/*     */ 
/* 136 */       this.read_insert = null;
/*     */     }
/*     */     
/*     */ 
/* 140 */     return len + this.transport.read(buffers, array_offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int read(ByteBuffer buffer)
/*     */     throws IOException
/*     */   {
/* 150 */     if (this.read_insert != null)
/*     */     {
/* 152 */       return (int)read(new ByteBuffer[] { buffer }, 0, 1);
/*     */     }
/*     */     
/* 155 */     return this.transport.read(buffer);
/*     */   }
/*     */   
/*     */ 
/*     */   public TransportHelper getHelper()
/*     */   {
/* 161 */     return this.transport;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setTrace(boolean on)
/*     */   {
/* 168 */     this.transport.setTrace(on);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isEncrypted()
/*     */   {
/* 174 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName(boolean verbose)
/*     */   {
/* 180 */     String proto_str = getHelper().getName(verbose);
/*     */     
/* 182 */     if (proto_str.length() > 0)
/*     */     {
/* 184 */       proto_str = " (" + proto_str + ")";
/*     */     }
/*     */     
/* 187 */     return (this.is_plain ? "Plain" : "None") + proto_str;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/TransportHelperFilterTransparent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */