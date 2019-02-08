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
/*     */ public class BTHave
/*     */   implements BTMessage
/*     */ {
/*     */   private final byte version;
/*  33 */   private DirectByteBuffer buffer = null;
/*  34 */   private String description = null;
/*     */   
/*     */   private final int piece_number;
/*     */   
/*     */   public BTHave(int piece_number, byte version)
/*     */   {
/*  40 */     this.piece_number = piece_number;
/*  41 */     this.version = version;
/*     */   }
/*     */   
/*     */   public int getPieceNumber() {
/*  45 */     return this.piece_number;
/*     */   }
/*     */   
/*  48 */   public String getID() { return "BT_HAVE"; }
/*  49 */   public byte[] getIDBytes() { return BTMessage.ID_BT_HAVE_BYTES; }
/*     */   
/*  51 */   public String getFeatureID() { return "BT1"; }
/*     */   
/*  53 */   public int getFeatureSubID() { return 4; }
/*     */   
/*  55 */   public int getType() { return 0; }
/*     */   
/*  57 */   public byte getVersion() { return this.version; }
/*     */   
/*     */   public String getDescription() {
/*  60 */     if (this.description == null) {
/*  61 */       this.description = ("BT_HAVE piece #" + this.piece_number);
/*     */     }
/*     */     
/*  64 */     return this.description;
/*     */   }
/*     */   
/*     */   public DirectByteBuffer[] getData()
/*     */   {
/*  69 */     if (this.buffer == null) {
/*  70 */       this.buffer = DirectByteBufferPool.getBuffer((byte)17, 4);
/*  71 */       this.buffer.putInt((byte)11, this.piece_number);
/*  72 */       this.buffer.flip((byte)11);
/*     */     }
/*     */     
/*  75 */     return new DirectByteBuffer[] { this.buffer };
/*     */   }
/*     */   
/*     */   public Message deserialize(DirectByteBuffer data, byte version) throws MessageException
/*     */   {
/*  80 */     if (data == null) {
/*  81 */       throw new MessageException("[" + getID() + "] decode error: data == null");
/*     */     }
/*     */     
/*  84 */     if (data.remaining((byte)11) != 4) {
/*  85 */       throw new MessageException("[" + getID() + "] decode error: payload.remaining[" + data.remaining((byte)11) + "] != 4");
/*     */     }
/*     */     
/*  88 */     int number = data.getInt((byte)11);
/*     */     
/*  90 */     if (number < 0) {
/*  91 */       throw new MessageException("[" + getID() + "] decode error: number < 0");
/*     */     }
/*     */     
/*  94 */     data.returnToPool();
/*     */     
/*  96 */     return new BTHave(number, version);
/*     */   }
/*     */   
/*     */   public void destroy()
/*     */   {
/* 101 */     if (this.buffer != null) this.buffer.returnToPool();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/BTHave.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */