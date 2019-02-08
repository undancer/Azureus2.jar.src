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
/*     */ 
/*     */ public class BTRequest
/*     */   implements BTMessage
/*     */ {
/*     */   private final byte version;
/*  35 */   private DirectByteBuffer buffer = null;
/*  36 */   private String description = null;
/*     */   
/*     */   private final int piece_number;
/*     */   private final int piece_offset;
/*     */   private final int length;
/*     */   private final int hashcode;
/*     */   
/*     */   public BTRequest(int piece_number, int piece_offset, int length, byte version)
/*     */   {
/*  45 */     this.piece_number = piece_number;
/*  46 */     this.piece_offset = piece_offset;
/*  47 */     this.length = length;
/*  48 */     this.version = version;
/*  49 */     this.hashcode = (piece_number + piece_offset + length);
/*     */   }
/*     */   
/*     */ 
/*  53 */   public int getPieceNumber() { return this.piece_number; }
/*     */   
/*  55 */   public int getPieceOffset() { return this.piece_offset; }
/*     */   
/*  57 */   public int getLength() { return this.length; }
/*     */   
/*     */ 
/*     */ 
/*  61 */   public String getID() { return "BT_REQUEST"; }
/*  62 */   public byte[] getIDBytes() { return BTMessage.ID_BT_REQUEST_BYTES; }
/*     */   
/*  64 */   public String getFeatureID() { return "BT1"; }
/*     */   
/*  66 */   public int getFeatureSubID() { return 6; }
/*     */   
/*  68 */   public int getType() { return 0; }
/*     */   
/*  70 */   public byte getVersion() { return this.version; }
/*     */   
/*     */   public String getDescription() {
/*  73 */     if (this.description == null) {
/*  74 */       this.description = ("BT_REQUEST piece #" + this.piece_number + ":" + this.piece_offset + "->" + (this.piece_offset + this.length - 1));
/*     */     }
/*     */     
/*  77 */     return this.description;
/*     */   }
/*     */   
/*     */   public DirectByteBuffer[] getData()
/*     */   {
/*  82 */     if (this.buffer == null) {
/*  83 */       this.buffer = DirectByteBufferPool.getBuffer((byte)19, 12);
/*  84 */       this.buffer.putInt((byte)11, this.piece_number);
/*  85 */       this.buffer.putInt((byte)11, this.piece_offset);
/*  86 */       this.buffer.putInt((byte)11, this.length);
/*  87 */       this.buffer.flip((byte)11);
/*     */     }
/*     */     
/*  90 */     return new DirectByteBuffer[] { this.buffer };
/*     */   }
/*     */   
/*     */   public Message deserialize(DirectByteBuffer data, byte version) throws MessageException
/*     */   {
/*  95 */     if (data == null) {
/*  96 */       throw new MessageException("[" + getID() + "] decode error: data == null");
/*     */     }
/*     */     
/*  99 */     if (data.remaining((byte)11) != 12) {
/* 100 */       throw new MessageException("[" + getID() + "] decode error: payload.remaining[" + data.remaining((byte)11) + "] != 12");
/*     */     }
/*     */     
/* 103 */     int num = data.getInt((byte)11);
/* 104 */     if (num < 0) {
/* 105 */       throw new MessageException("[" + getID() + "] decode error: num < 0");
/*     */     }
/*     */     
/* 108 */     int offset = data.getInt((byte)11);
/* 109 */     if (offset < 0) {
/* 110 */       throw new MessageException("[" + getID() + "] decode error: offset < 0");
/*     */     }
/*     */     
/* 113 */     int length = data.getInt((byte)11);
/* 114 */     if (length < 0) {
/* 115 */       throw new MessageException("[" + getID() + "] decode error: length < 0");
/*     */     }
/*     */     
/* 118 */     data.returnToPool();
/*     */     
/* 120 */     return new BTRequest(num, offset, length, version);
/*     */   }
/*     */   
/*     */   public void destroy()
/*     */   {
/* 125 */     if (this.buffer != null) { this.buffer.returnToPool();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean equals(Object obj)
/*     */   {
/* 131 */     if (this == obj) return true;
/* 132 */     if ((obj != null) && ((obj instanceof BTRequest))) {
/* 133 */       BTRequest other = (BTRequest)obj;
/* 134 */       if ((other.piece_number == this.piece_number) && (other.piece_offset == this.piece_offset) && (other.length == this.length))
/*     */       {
/* 136 */         return true; }
/*     */     }
/* 138 */     return false;
/*     */   }
/*     */   
/* 141 */   public int hashCode() { return this.hashcode; }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/BTRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */