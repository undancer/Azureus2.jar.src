/*    */ package com.aelitis.azureus.core.peermanager.messaging.bittorrent;
/*    */ 
/*    */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*    */ import com.aelitis.azureus.core.peermanager.messaging.MessageException;
/*    */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*    */ import org.gudy.azureus2.core3.util.DirectByteBufferPool;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BTAllowedFast
/*    */   implements BTMessage
/*    */ {
/* 30 */   private DirectByteBuffer buffer = null;
/*    */   private final byte version;
/* 32 */   private String description = null;
/*    */   
/*    */   private final int piece_number;
/*    */   
/*    */   public BTAllowedFast(int piece_number, byte version)
/*    */   {
/* 38 */     this.piece_number = piece_number;
/* 39 */     this.version = version;
/*    */   }
/*    */   
/*    */   public int getPieceNumber()
/*    */   {
/* 44 */     return this.piece_number;
/*    */   }
/*    */   
/* 47 */   public String getID() { return "BT_ALLOWED_FAST"; }
/* 48 */   public byte[] getIDBytes() { return BTMessage.ID_BT_ALLOWED_FAST_BYTES; }
/*    */   
/* 50 */   public String getFeatureID() { return "BT1"; }
/*    */   
/* 52 */   public int getFeatureSubID() { return 17; }
/*    */   
/* 54 */   public int getType() { return 0; }
/*    */   
/* 56 */   public byte getVersion() { return this.version; }
/*    */   
/*    */   public String getDescription() {
/* 59 */     if (this.description == null) {
/* 60 */       this.description = ("BT_ALLOWED_FAST piece #" + this.piece_number);
/*    */     }
/*    */     
/* 63 */     return this.description;
/*    */   }
/*    */   
/*    */   public DirectByteBuffer[] getData()
/*    */   {
/* 68 */     if (this.buffer == null) {
/* 69 */       this.buffer = DirectByteBufferPool.getBuffer((byte)33, 4);
/* 70 */       this.buffer.putInt((byte)11, this.piece_number);
/* 71 */       this.buffer.flip((byte)11);
/*    */     }
/*    */     
/* 74 */     return new DirectByteBuffer[] { this.buffer };
/*    */   }
/*    */   
/*    */   public Message deserialize(DirectByteBuffer data, byte version) throws MessageException
/*    */   {
/* 79 */     if (data == null) {
/* 80 */       throw new MessageException("[" + getID() + "] decode error: data == null");
/*    */     }
/*    */     
/* 83 */     if (data.remaining((byte)11) != 4) {
/* 84 */       throw new MessageException("[" + getID() + "] decode error: payload.remaining[" + data.remaining((byte)11) + "] != 12");
/*    */     }
/*    */     
/* 87 */     int num = data.getInt((byte)11);
/* 88 */     if (num < 0) {
/* 89 */       throw new MessageException("[" + getID() + "] decode error: num < 0");
/*    */     }
/*    */     
/* 92 */     data.returnToPool();
/*    */     
/* 94 */     return new BTAllowedFast(num, version);
/*    */   }
/*    */   
/*    */   public void destroy()
/*    */   {
/* 99 */     if (this.buffer != null) this.buffer.returnToPool();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/BTAllowedFast.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */