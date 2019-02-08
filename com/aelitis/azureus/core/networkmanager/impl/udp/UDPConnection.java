/*     */ package com.aelitis.azureus.core.networkmanager.impl.udp;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
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
/*     */ public class UDPConnection
/*     */ {
/*     */   private final UDPConnectionSet set;
/*     */   private int id;
/*     */   private UDPTransportHelper transport;
/*  36 */   private final List read_buffers = new LinkedList();
/*     */   
/*  38 */   private final AESemaphore read_buffer_sem = new AESemaphore("UDPConnection", 64);
/*     */   
/*     */ 
/*  41 */   private volatile boolean connected = true;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected UDPConnection(UDPConnectionSet _set, int _id, UDPTransportHelper _transport)
/*     */   {
/*  49 */     this.set = _set;
/*  50 */     this.id = _id;
/*  51 */     this.transport = _transport;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected UDPConnection(UDPConnectionSet _set, int _id)
/*     */   {
/*  59 */     this.set = _set;
/*  60 */     this.id = _id;
/*     */   }
/*     */   
/*     */ 
/*     */   protected UDPSelector getSelector()
/*     */   {
/*  66 */     return this.set.getSelector();
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getID()
/*     */   {
/*  72 */     return this.id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setID(int _id)
/*     */   {
/*  79 */     this.id = _id;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isIncoming()
/*     */   {
/*  85 */     return this.transport.isIncoming();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setSecret(byte[] session_secret)
/*     */   {
/*  92 */     this.set.setSecret(this, session_secret);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setTransport(UDPTransportHelper _transport)
/*     */   {
/*  99 */     this.transport = _transport;
/*     */   }
/*     */   
/*     */ 
/*     */   protected UDPTransportHelper getTransport()
/*     */   {
/* 105 */     return this.transport;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void receive(ByteBuffer data)
/*     */     throws IOException
/*     */   {
/* 117 */     int rem = data.remaining();
/*     */     
/* 119 */     if (rem < 256)
/*     */     {
/* 121 */       byte[] temp = new byte[rem];
/*     */       
/* 123 */       data.get(temp);
/*     */       
/* 125 */       data = ByteBuffer.wrap(temp);
/*     */     }
/*     */     
/* 128 */     this.read_buffer_sem.reserve();
/*     */     
/* 130 */     if (!this.connected)
/*     */     {
/* 132 */       throw new IOException("Transport closed");
/*     */     }
/*     */     
/* 135 */     boolean was_empty = false;
/*     */     
/* 137 */     synchronized (this.read_buffers)
/*     */     {
/* 139 */       was_empty = this.read_buffers.size() == 0;
/*     */       
/* 141 */       this.read_buffers.add(data);
/*     */     }
/*     */     
/* 144 */     if (was_empty)
/*     */     {
/* 146 */       this.transport.canRead();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void sent()
/*     */   {
/* 155 */     this.transport.canWrite();
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean canRead()
/*     */   {
/* 161 */     synchronized (this.read_buffers)
/*     */     {
/* 163 */       return this.read_buffers.size() > 0;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean canWrite()
/*     */   {
/* 170 */     return this.set.canWrite(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int write(ByteBuffer[] buffers, int offset, int length)
/*     */     throws IOException
/*     */   {
/* 181 */     int written = this.set.write(this, buffers, offset, length);
/*     */     
/*     */ 
/*     */ 
/* 185 */     return written;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int read(ByteBuffer buffer)
/*     */     throws IOException
/*     */   {
/* 194 */     int total = 0;
/*     */     
/* 196 */     synchronized (this.read_buffers)
/*     */     {
/* 198 */       while (this.read_buffers.size() > 0)
/*     */       {
/* 200 */         int rem = buffer.remaining();
/*     */         
/* 202 */         if (rem == 0) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/* 207 */         ByteBuffer b = (ByteBuffer)this.read_buffers.get(0);
/*     */         
/* 209 */         int old_limit = b.limit();
/*     */         
/* 211 */         if (b.remaining() > rem)
/*     */         {
/* 213 */           b.limit(b.position() + rem);
/*     */         }
/*     */         
/* 216 */         buffer.put(b);
/*     */         
/* 218 */         b.limit(old_limit);
/*     */         
/* 220 */         total += rem - buffer.remaining();
/*     */         
/* 222 */         if (b.hasRemaining()) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 228 */         this.read_buffers.remove(0);
/*     */         
/* 230 */         this.read_buffer_sem.release();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 237 */     return total;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void close(String reason)
/*     */   {
/* 244 */     if (this.transport != null)
/*     */     {
/* 246 */       this.transport.close(reason);
/*     */     }
/*     */     else
/*     */     {
/* 250 */       closeSupport(reason);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void failed(Throwable reason)
/*     */   {
/* 258 */     if (this.transport != null)
/*     */     {
/* 260 */       this.transport.failed(reason);
/*     */     }
/*     */     else
/*     */     {
/* 264 */       failedSupport(reason);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void closeSupport(String reason)
/*     */   {
/* 272 */     this.connected = false;
/*     */     
/* 274 */     this.read_buffer_sem.releaseForever();
/*     */     
/* 276 */     this.set.close(this, reason);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void failedSupport(Throwable reason)
/*     */   {
/* 283 */     this.connected = false;
/*     */     
/* 285 */     this.read_buffer_sem.releaseForever();
/*     */     
/* 287 */     this.set.failed(this, reason);
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isConnected()
/*     */   {
/* 293 */     return this.connected;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void poll()
/*     */   {
/* 299 */     if (this.transport != null)
/*     */     {
/* 301 */       this.transport.poll();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/udp/UDPConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */