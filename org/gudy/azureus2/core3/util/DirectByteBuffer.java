/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.SocketChannel;
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
/*     */ public class DirectByteBuffer
/*     */ {
/*     */   public static final byte AL_NONE = 0;
/*     */   public static final byte AL_EXTERNAL = 1;
/*     */   public static final byte AL_OTHER = 2;
/*     */   public static final byte AL_PT_READ = 3;
/*     */   public static final byte AL_PT_LENGTH = 4;
/*     */   public static final byte AL_CACHE_READ = 5;
/*     */   public static final byte AL_DM_READ = 6;
/*     */   public static final byte AL_DM_ZERO = 7;
/*     */   public static final byte AL_DM_CHECK = 8;
/*     */   public static final byte AL_BT_PIECE = 9;
/*     */   public static final byte AL_CACHE_WRITE = 10;
/*     */   public static final byte AL_PROXY_RELAY = 11;
/*     */   public static final byte AL_MSG = 12;
/*     */   public static final byte AL_MSG_AZ_HAND = 13;
/*     */   public static final byte AL_MSG_AZ_PEX = 14;
/*     */   public static final byte AL_MSG_BT_CANCEL = 15;
/*     */   public static final byte AL_MSG_BT_HAND = 16;
/*     */   public static final byte AL_MSG_BT_HAVE = 17;
/*     */   public static final byte AL_MSG_BT_PIECE = 18;
/*     */   public static final byte AL_MSG_BT_REQUEST = 19;
/*     */   public static final byte AL_MSG_BT_KEEPALIVE = 20;
/*     */   public static final byte AL_MSG_BT_HEADER = 21;
/*     */   public static final byte AL_MSG_AZ_HEADER = 22;
/*     */   public static final byte AL_MSG_BT_PAYLOAD = 23;
/*     */   public static final byte AL_MSG_AZ_PAYLOAD = 24;
/*     */   public static final byte AL_FILE = 25;
/*     */   public static final byte AL_NET_CRYPT = 26;
/*     */   public static final byte AL_MSG_LT_EXT_MESSAGE = 27;
/*     */   public static final byte AL_MSG_LT_HANDSHAKE = 28;
/*     */   public static final byte AL_MSG_UT_PEX = 29;
/*     */   public static final byte AL_MSG_BT_DHT_PORT = 30;
/*     */   public static final byte AL_MSG_BT_REJECT_REQUEST = 31;
/*     */   public static final byte AL_MSG_BT_SUGGEST_PIECE = 32;
/*     */   public static final byte AL_MSG_BT_ALLOWED_FAST = 33;
/*     */   public static final byte AL_MSG_UT_METADATA = 34;
/*     */   public static final byte AL_MSG_AZ_METADATA = 35;
/*     */   public static final byte AL_MSG_UT_UPLOAD_ONLY = 36;
/*  79 */   public static final String[] AL_DESCS = { "None", "Ext", "Other", "PeerRead", "PeerLen", "CacheRead", "DiskRead", "DiskZero", "DiskCheck", "BTPiece", "CacheWrite", "ProxyRelay", "Messaging", "AZHandshake", "AZPEX", "BTCancel", "BTHandshake", "BTHave", "BTPiece", "BTRequest", "BTKeepAlive", "BTHeader", "AZHeader", "BTPayload", "AZPayload", "File", "MsgCrypt", "LTExtMsg", "LTExtHandshake", "UTPEX", "BTDHTPort", "BTRejectRequest", "BTSuggestPiece", "BTAllowedFast", "UTMetaData", "AZMetaData", "UTUploadOnly" };
/*     */   
/*     */ 
/*     */ 
/*     */   public static final byte SS_NONE = 0;
/*     */   
/*     */ 
/*     */ 
/*     */   public static final byte SS_EXTERNAL = 1;
/*     */   
/*     */ 
/*     */ 
/*     */   public static final byte SS_OTHER = 2;
/*     */   
/*     */ 
/*     */ 
/*     */   public static final byte SS_CACHE = 3;
/*     */   
/*     */ 
/*     */ 
/*     */   public static final byte SS_FILE = 4;
/*     */   
/*     */ 
/*     */ 
/*     */   public static final byte SS_NET = 5;
/*     */   
/*     */ 
/*     */ 
/*     */   public static final byte SS_BT = 6;
/*     */   
/*     */ 
/*     */   public static final byte SS_DR = 7;
/*     */   
/*     */ 
/*     */   public static final byte SS_DW = 8;
/*     */   
/*     */ 
/*     */   public static final byte SS_PEER = 9;
/*     */   
/*     */ 
/*     */   public static final byte SS_PROXY = 10;
/*     */   
/*     */ 
/*     */   public static final byte SS_MSG = 11;
/*     */   
/*     */ 
/* 125 */   public static final String[] SS_DESCS = { "None", "Ext", "Other", "Cache", "File", "Net", "BT", "DiskRead", "DiskWrite", "Peer", "Proxy", "Messaging" };
/*     */   
/*     */   public static final byte OP_LIMIT = 0;
/*     */   
/*     */   public static final byte OP_LIMIT_INT = 1;
/*     */   
/*     */   public static final byte OP_POSITION = 2;
/*     */   
/*     */   public static final byte OP_POSITION_INT = 3;
/*     */   
/*     */   public static final byte OP_CLEAR = 4;
/*     */   public static final byte OP_FLIP = 5;
/*     */   public static final byte OP_REMANING = 6;
/*     */   public static final byte OP_CAPACITY = 7;
/*     */   public static final byte OP_PUT_BYTEARRAY = 8;
/*     */   public static final byte OP_PUT_DBB = 9;
/*     */   public static final byte OP_PUT_BB = 10;
/*     */   public static final byte OP_PUTINT = 11;
/*     */   public static final byte OP_PUT_BYTE = 12;
/*     */   public static final byte OP_GET = 13;
/*     */   public static final byte OP_GET_INT = 14;
/*     */   public static final byte OP_GET_BYTEARRAY = 15;
/*     */   public static final byte OP_GETINT = 16;
/*     */   public static final byte OP_GETINT_INT = 17;
/*     */   public static final byte OP_HASREMAINING = 18;
/*     */   public static final byte OP_READ_FC = 19;
/*     */   public static final byte OP_WRITE_FC = 20;
/*     */   public static final byte OP_READ_SC = 21;
/*     */   public static final byte OP_WRITE_SC = 22;
/*     */   public static final byte OP_GETBUFFER = 23;
/*     */   public static final byte OP_GETSHORT = 24;
/*     */   public static final byte OP_PUTSHORT = 25;
/* 157 */   public static final String[] OP_DESCS = { "limit", "limit(int)", "position", "position(int)", "clear", "flip", "remaining", "capacity", "put(byte[])", "put(dbb)", "put(bbb)", "putInt", "put(byte)", "get", "get(int)", "get(byte[])", "getInt", "getInt(int", "hasRemaining", "read(fc)", "write(fc)", "read(sc)", "write(sc)", "getBuffer", "getShort", "putShort" };
/*     */   
/*     */ 
/*     */   public static final byte FL_NONE = 0;
/*     */   
/*     */ 
/*     */   public static final byte FL_CONTAINS_TRANSIENT_DATA = 1;
/*     */   
/*     */ 
/*     */   protected static final boolean TRACE = false;
/*     */   
/*     */ 
/*     */   protected static final int TRACE_BUFFER_SIZE = 64;
/*     */   
/*     */ 
/*     */   private ByteBuffer buffer;
/*     */   
/*     */ 
/*     */   private DirectByteBufferPool pool;
/*     */   
/*     */ 
/*     */   private byte allocator;
/*     */   
/*     */ 
/*     */   private byte flags;
/*     */   
/*     */ 
/*     */ 
/*     */   public DirectByteBuffer(ByteBuffer _buffer)
/*     */   {
/* 187 */     this((byte)0, _buffer, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DirectByteBuffer(byte _allocator, ByteBuffer _buffer, DirectByteBufferPool _pool)
/*     */   {
/* 196 */     if (_buffer == null) throw new NullPointerException("buffer is null");
/* 197 */     this.allocator = _allocator;
/* 198 */     this.buffer = _buffer;
/* 199 */     this.pool = _pool;
/*     */   }
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
/*     */   protected DirectByteBuffer(DirectByteBuffer basis)
/*     */   {
/* 219 */     this.allocator = basis.allocator;
/* 220 */     this.buffer = basis.buffer;
/* 221 */     this.pool = null;
/*     */     
/* 223 */     if (this.buffer == null) { throw new NullPointerException("basis.buffer is null");
/*     */     }
/*     */   }
/*     */   
/*     */   public ReferenceCountedDirectByteBuffer getReferenceCountedBuffer()
/*     */   {
/* 229 */     ReferenceCountedDirectByteBuffer res = new ReferenceCountedDirectByteBuffer(this);
/*     */     
/* 231 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setFlag(byte flag)
/*     */   {
/* 238 */     this.flags = ((byte)(this.flags | flag));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean getFlag(byte flag)
/*     */   {
/* 245 */     return (this.flags & flag) != 0;
/*     */   }
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
/*     */ 
/*     */ 
/*     */   protected void traceUsage(byte subsystem, byte operation) {}
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
/*     */ 
/*     */ 
/*     */   protected String getTraceString()
/*     */   {
/* 322 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void dumpTrace(Throwable e) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ByteBuffer getBufferInternal()
/*     */   {
/* 340 */     return this.buffer;
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte getAllocator()
/*     */   {
/* 346 */     return this.allocator;
/*     */   }
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
/*     */   public int limit(byte subsystem)
/*     */   {
/* 363 */     return this.buffer.limit();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void limit(byte subsystem, int l)
/*     */   {
/* 376 */     this.buffer.limit(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int position(byte subsystem)
/*     */   {
/* 388 */     return this.buffer.position();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void position(byte subsystem, int l)
/*     */   {
/* 401 */     this.buffer.position(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void clear(byte subsystem)
/*     */   {
/* 413 */     this.buffer.clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void flip(byte subsystem)
/*     */   {
/* 425 */     this.buffer.flip();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int remaining(byte subsystem)
/*     */   {
/* 437 */     return this.buffer.remaining();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int capacity(byte subsystem)
/*     */   {
/* 449 */     return this.buffer.capacity();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void put(byte subsystem, byte[] data)
/*     */   {
/* 462 */     this.buffer.put(data);
/*     */   }
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
/*     */   public void put(byte subsystem, byte[] data, int offset, int length)
/*     */   {
/* 477 */     this.buffer.put(data, offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void put(byte subsystem, DirectByteBuffer data)
/*     */   {
/* 490 */     this.buffer.put(data.buffer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void put(byte subsystem, ByteBuffer data)
/*     */   {
/* 503 */     this.buffer.put(data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void put(byte subsystem, byte data)
/*     */   {
/* 516 */     this.buffer.put(data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void putShort(byte subsystem, short x)
/*     */   {
/* 529 */     this.buffer.putShort(x);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void putInt(byte subsystem, int data)
/*     */   {
/* 542 */     this.buffer.putInt(data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte get(byte subsystem)
/*     */   {
/* 554 */     return this.buffer.get();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte get(byte subsystem, int x)
/*     */   {
/* 567 */     return this.buffer.get(x);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void get(byte subsystem, byte[] data)
/*     */   {
/* 580 */     this.buffer.get(data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public short getShort(byte subsystem)
/*     */   {
/* 592 */     return this.buffer.getShort();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getInt(byte subsystem)
/*     */   {
/* 604 */     return this.buffer.getInt();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getInt(byte subsystem, int x)
/*     */   {
/* 617 */     return this.buffer.getInt(x);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasRemaining(byte subsystem)
/*     */   {
/* 629 */     return this.buffer.hasRemaining();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int read(byte subsystem, FileChannel chan)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 645 */       return chan.read(this.buffer);
/*     */     }
/*     */     catch (IllegalArgumentException e)
/*     */     {
/* 649 */       dumpTrace(e);
/*     */       
/* 651 */       throw e;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int write(byte subsystem, FileChannel chan)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 668 */       return chan.write(this.buffer);
/*     */     }
/*     */     catch (IllegalArgumentException e)
/*     */     {
/* 672 */       dumpTrace(e);
/*     */       
/* 674 */       throw e;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int read(byte subsystem, SocketChannel chan)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 691 */       return chan.read(this.buffer);
/*     */     }
/*     */     catch (IllegalArgumentException e)
/*     */     {
/* 695 */       dumpTrace(e);
/*     */       
/* 697 */       throw e;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int write(byte subsystem, SocketChannel chan)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 714 */       return chan.write(this.buffer);
/*     */     }
/*     */     catch (IllegalArgumentException e)
/*     */     {
/* 718 */       dumpTrace(e);
/*     */       
/* 720 */       throw e;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ByteBuffer getBuffer(byte subsystem)
/*     */   {
/* 733 */     return this.buffer;
/*     */   }
/*     */   
/*     */ 
/*     */   public void returnToPool()
/*     */   {
/* 739 */     if (this.pool != null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 745 */       synchronized (this)
/*     */       {
/* 747 */         if (this.buffer == null)
/*     */         {
/* 749 */           Debug.out("Buffer already returned to pool");
/*     */         }
/*     */         else
/*     */         {
/* 753 */           this.pool.returnBufferSupport(this);
/*     */           
/* 755 */           this.buffer = null;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
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
/*     */   public void returnToPoolIfNotFree()
/*     */   {
/* 772 */     if (this.pool != null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 778 */       synchronized (this)
/*     */       {
/* 780 */         if (this.buffer != null)
/*     */         {
/* 782 */           this.pool.returnBufferSupport(this);
/*     */           
/* 784 */           this.buffer = null;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/DirectByteBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */