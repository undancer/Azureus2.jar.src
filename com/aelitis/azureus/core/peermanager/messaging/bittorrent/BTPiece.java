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
/*     */ public class BTPiece
/*     */   implements BTMessage
/*     */ {
/*     */   private final byte version;
/*  34 */   private final DirectByteBuffer[] buffer = new DirectByteBuffer[2];
/*     */   
/*     */   private String description;
/*     */   private final int piece_number;
/*     */   private final int piece_offset;
/*     */   private final int piece_length;
/*     */   
/*     */   public BTPiece(int piece_number, int piece_offset, DirectByteBuffer data, byte version)
/*     */   {
/*  43 */     this.piece_number = piece_number;
/*  44 */     this.piece_offset = piece_offset;
/*  45 */     this.piece_length = (data == null ? 0 : data.remaining((byte)11));
/*  46 */     this.buffer[1] = data;
/*  47 */     this.version = version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  52 */   public int getPieceNumber() { return this.piece_number; }
/*     */   
/*  54 */   public int getPieceOffset() { return this.piece_offset; }
/*     */   
/*  56 */   public DirectByteBuffer getPieceData() { return this.buffer[1]; }
/*     */   
/*     */ 
/*     */ 
/*  60 */   public String getID() { return "BT_PIECE"; }
/*  61 */   public byte[] getIDBytes() { return BTMessage.ID_BT_PIECE_BYTES; }
/*     */   
/*  63 */   public String getFeatureID() { return "BT1"; }
/*     */   
/*  65 */   public int getFeatureSubID() { return 7; }
/*     */   
/*  67 */   public int getType() { return 1; }
/*     */   
/*  69 */   public byte getVersion() { return this.version; }
/*     */   
/*     */   public String getDescription() {
/*  72 */     if (this.description == null) {
/*  73 */       this.description = ("BT_PIECE data for piece #" + this.piece_number + ":" + this.piece_offset + "->" + (this.piece_offset + this.piece_length - 1));
/*     */     }
/*     */     
/*  76 */     return this.description;
/*     */   }
/*     */   
/*     */   public DirectByteBuffer[] getData()
/*     */   {
/*  81 */     if (this.buffer[0] == null) {
/*  82 */       this.buffer[0] = DirectByteBufferPool.getBuffer(18, 8);
/*  83 */       this.buffer[0].putInt((byte)11, this.piece_number);
/*  84 */       this.buffer[0].putInt((byte)11, this.piece_offset);
/*  85 */       this.buffer[0].flip((byte)11);
/*     */     }
/*     */     
/*  88 */     return this.buffer;
/*     */   }
/*     */   
/*     */   public Message deserialize(DirectByteBuffer data, byte version)
/*     */     throws MessageException
/*     */   {
/*  94 */     if (data == null) {
/*  95 */       throw new MessageException("[" + getID() + "] decode error: data == null");
/*     */     }
/*     */     
/*  98 */     if (data.remaining((byte)11) < 8) {
/*  99 */       throw new MessageException("[" + getID() + "] decode error: payload.remaining[" + data.remaining((byte)11) + "] < 8");
/*     */     }
/*     */     
/* 102 */     int number = data.getInt((byte)11);
/* 103 */     if (number < 0) {
/* 104 */       throw new MessageException("[" + getID() + "] decode error: number < 0");
/*     */     }
/*     */     
/* 107 */     int offset = data.getInt((byte)11);
/* 108 */     if (offset < 0) {
/* 109 */       throw new MessageException("[" + getID() + "] decode error: offset < 0");
/*     */     }
/*     */     
/* 112 */     return new BTPiece(number, offset, data, version);
/*     */   }
/*     */   
/*     */   public void destroy()
/*     */   {
/* 117 */     if (this.buffer[0] != null) this.buffer[0].returnToPool();
/* 118 */     if (this.buffer[1] != null) this.buffer[1].returnToPool();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/BTPiece.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */