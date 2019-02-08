/*     */ package com.aelitis.azureus.core.peermanager.messaging.bittorrent;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageException;
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
/*     */ public class BTRejectRequest
/*     */   implements BTMessage
/*     */ {
/*     */   private final byte version;
/*  34 */   private DirectByteBuffer buffer = null;
/*  35 */   private String description = null;
/*     */   
/*     */   private final int piece_number;
/*     */   private final int piece_offset;
/*     */   private final int length;
/*     */   private final int hashcode;
/*     */   
/*     */   public BTRejectRequest(int piece_number, int piece_offset, int length, byte version)
/*     */   {
/*  44 */     this.piece_number = piece_number;
/*  45 */     this.piece_offset = piece_offset;
/*  46 */     this.length = length;
/*  47 */     this.version = version;
/*  48 */     this.hashcode = (piece_number + piece_offset + length);
/*     */   }
/*     */   
/*     */ 
/*  52 */   public int getPieceNumber() { return this.piece_number; }
/*     */   
/*  54 */   public int getPieceOffset() { return this.piece_offset; }
/*     */   
/*  56 */   public int getLength() { return this.length; }
/*     */   
/*     */ 
/*     */ 
/*  60 */   public String getID() { return "BT_REJECT_REQUEST"; }
/*  61 */   public byte[] getIDBytes() { return BTMessage.ID_BT_REJECT_REQUEST_BYTES; }
/*     */   
/*  63 */   public String getFeatureID() { return "BT1"; }
/*     */   
/*  65 */   public int getFeatureSubID() { return 16; }
/*     */   
/*  67 */   public int getType() { return 0; }
/*     */   
/*  69 */   public byte getVersion() { return this.version; }
/*     */   
/*     */   public String getDescription() {
/*  72 */     if (this.description == null) {
/*  73 */       this.description = ("BT_REJECT_REQUEST piece #" + this.piece_number + ":" + this.piece_offset + "->" + (this.piece_offset + this.length - 1));
/*     */     }
/*     */     
/*  76 */     return this.description;
/*     */   }
/*     */   
/*     */   public DirectByteBuffer[] getData()
/*     */   {
/*  81 */     if (this.buffer == null) {
/*  82 */       this.buffer = DirectByteBufferPool.getBuffer((byte)31, 12);
/*  83 */       this.buffer.putInt((byte)11, this.piece_number);
/*  84 */       this.buffer.putInt((byte)11, this.piece_offset);
/*  85 */       this.buffer.putInt((byte)11, this.length);
/*  86 */       this.buffer.flip((byte)11);
/*     */     }
/*     */     
/*  89 */     return new DirectByteBuffer[] { this.buffer };
/*     */   }
/*     */   
/*     */   public Message deserialize(DirectByteBuffer data, byte version) throws MessageException
/*     */   {
/*  94 */     if (data == null) {
/*  95 */       throw new MessageException("[" + getID() + "] decode error: data == null");
/*     */     }
/*     */     
/*  98 */     if (data.remaining((byte)11) != 12) {
/*  99 */       throw new MessageException("[" + getID() + "] decode error: payload.remaining[" + data.remaining((byte)11) + "] != 12");
/*     */     }
/*     */     
/* 102 */     int num = data.getInt((byte)11);
/* 103 */     if (num < 0) {
/* 104 */       throw new MessageException("[" + getID() + "] decode error: num < 0");
/*     */     }
/*     */     
/* 107 */     int offset = data.getInt((byte)11);
/* 108 */     if (offset < 0) {
/* 109 */       throw new MessageException("[" + getID() + "] decode error: offset < 0");
/*     */     }
/*     */     
/* 112 */     int length = data.getInt((byte)11);
/* 113 */     if (length < 0) {
/* 114 */       throw new MessageException("[" + getID() + "] decode error: length < 0");
/*     */     }
/*     */     
/* 117 */     data.returnToPool();
/*     */     
/* 119 */     return new BTRejectRequest(num, offset, length, version);
/*     */   }
/*     */   
/*     */   public void destroy()
/*     */   {
/* 124 */     if (this.buffer != null) { this.buffer.returnToPool();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean equals(Object obj)
/*     */   {
/* 130 */     if (this == obj) return true;
/* 131 */     if ((obj != null) && ((obj instanceof BTRejectRequest))) {
/* 132 */       BTRejectRequest other = (BTRejectRequest)obj;
/* 133 */       if ((other.piece_number == this.piece_number) && (other.piece_offset == this.piece_offset) && (other.length == this.length))
/*     */       {
/* 135 */         return true; }
/*     */     }
/* 137 */     return false;
/*     */   }
/*     */   
/* 140 */   public int hashCode() { return this.hashcode; }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/BTRejectRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */