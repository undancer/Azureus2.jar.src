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
/*     */ public class BTCancel
/*     */   implements BTMessage
/*     */ {
/*  33 */   private DirectByteBuffer buffer = null;
/*     */   private final byte version;
/*  35 */   private String description = null;
/*     */   
/*     */   private final int piece_number;
/*     */   private final int piece_offset;
/*     */   private final int length;
/*     */   
/*     */   public BTCancel(int piece_number, int piece_offset, int length, byte version)
/*     */   {
/*  43 */     this.piece_number = piece_number;
/*  44 */     this.piece_offset = piece_offset;
/*  45 */     this.length = length;
/*  46 */     this.version = version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  51 */   public int getPieceNumber() { return this.piece_number; }
/*     */   
/*  53 */   public int getPieceOffset() { return this.piece_offset; }
/*     */   
/*  55 */   public int getLength() { return this.length; }
/*     */   
/*     */ 
/*     */ 
/*  59 */   public String getID() { return "BT_CANCEL"; }
/*  60 */   public byte[] getIDBytes() { return BTMessage.ID_BT_CANCEL_BYTES; }
/*     */   
/*  62 */   public String getFeatureID() { return "BT1"; }
/*     */   
/*  64 */   public int getFeatureSubID() { return 8; }
/*     */   
/*  66 */   public int getType() { return 0; }
/*     */   
/*  68 */   public byte getVersion() { return this.version; }
/*     */   
/*     */   public String getDescription() {
/*  71 */     if (this.description == null) {
/*  72 */       this.description = ("BT_CANCEL piece #" + this.piece_number + ":" + this.piece_offset + "->" + (this.piece_offset + this.length - 1));
/*     */     }
/*     */     
/*  75 */     return this.description;
/*     */   }
/*     */   
/*     */   public DirectByteBuffer[] getData()
/*     */   {
/*  80 */     if (this.buffer == null) {
/*  81 */       this.buffer = DirectByteBufferPool.getBuffer((byte)15, 12);
/*  82 */       this.buffer.putInt((byte)11, this.piece_number);
/*  83 */       this.buffer.putInt((byte)11, this.piece_offset);
/*  84 */       this.buffer.putInt((byte)11, this.length);
/*  85 */       this.buffer.flip((byte)11);
/*     */     }
/*     */     
/*  88 */     return new DirectByteBuffer[] { this.buffer };
/*     */   }
/*     */   
/*     */   public Message deserialize(DirectByteBuffer data, byte version) throws MessageException
/*     */   {
/*  93 */     if (data == null) {
/*  94 */       throw new MessageException("[" + getID() + "] decode error: data == null");
/*     */     }
/*     */     
/*  97 */     if (data.remaining((byte)11) != 12) {
/*  98 */       throw new MessageException("[" + getID() + "] decode error: payload.remaining[" + data.remaining((byte)11) + "] != 12");
/*     */     }
/*     */     
/* 101 */     int num = data.getInt((byte)11);
/* 102 */     if (num < 0) {
/* 103 */       throw new MessageException("[" + getID() + "] decode error: num < 0");
/*     */     }
/*     */     
/* 106 */     int offset = data.getInt((byte)11);
/* 107 */     if (offset < 0) {
/* 108 */       throw new MessageException("[" + getID() + "] decode error: offset < 0");
/*     */     }
/*     */     
/* 111 */     int length = data.getInt((byte)11);
/* 112 */     if (length < 0) {
/* 113 */       throw new MessageException("[" + getID() + "] decode error: lngth < 0");
/*     */     }
/*     */     
/* 116 */     data.returnToPool();
/*     */     
/* 118 */     return new BTCancel(num, offset, length, version);
/*     */   }
/*     */   
/*     */   public void destroy()
/*     */   {
/* 123 */     if (this.buffer != null) this.buffer.returnToPool();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/BTCancel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */