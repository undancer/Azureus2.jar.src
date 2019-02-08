/*    */ package com.aelitis.azureus.core.peermanager.messaging.bittorrent;
/*    */ 
/*    */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*    */ import com.aelitis.azureus.core.peermanager.messaging.MessageException;
/*    */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BTBitfield
/*    */   implements BTMessage
/*    */ {
/*    */   private final DirectByteBuffer[] buffer;
/*    */   private final byte version;
/*    */   
/*    */   public BTBitfield(DirectByteBuffer bitfield, byte _version)
/*    */   {
/* 38 */     this.buffer = new DirectByteBuffer[] { bitfield };
/* 39 */     this.version = _version;
/*    */   }
/*    */   
/*    */   public DirectByteBuffer getBitfield() {
/* 43 */     return this.buffer[0];
/*    */   }
/*    */   
/*    */ 
/* 47 */   public String getID() { return "BT_BITFIELD"; }
/* 48 */   public byte[] getIDBytes() { return BTMessage.ID_BT_BITFIELD_BYTES; }
/*    */   
/* 50 */   public String getFeatureID() { return "BT1"; }
/*    */   
/* 52 */   public int getFeatureSubID() { return 5; }
/*    */   
/*    */ 
/* 55 */   public int getType() { return 0; }
/*    */   
/* 57 */   public byte getVersion() { return this.version; }
/*    */   
/* 59 */   public String getDescription() { return "BT_BITFIELD"; }
/*    */   
/* 61 */   public DirectByteBuffer[] getData() { return this.buffer; }
/*    */   
/*    */   public Message deserialize(DirectByteBuffer data, byte version) throws MessageException {
/* 64 */     if (data == null) {
/* 65 */       throw new MessageException("[" + getID() + "] decode error: data == null");
/*    */     }
/*    */     
/* 68 */     return new BTBitfield(data, version);
/*    */   }
/*    */   
/*    */   public void destroy() {
/* 72 */     if (this.buffer[0] != null) this.buffer[0].returnToPool();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/BTBitfield.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */