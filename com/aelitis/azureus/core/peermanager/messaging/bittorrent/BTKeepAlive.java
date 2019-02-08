/*    */ package com.aelitis.azureus.core.peermanager.messaging.bittorrent;
/*    */ 
/*    */ import com.aelitis.azureus.core.networkmanager.RawMessage;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BTKeepAlive
/*    */   implements BTMessage, RawMessage
/*    */ {
/*    */   private final byte version;
/* 35 */   private DirectByteBuffer[] buffer = null;
/*    */   
/* 37 */   private boolean no_delay = false;
/*    */   
/*    */   public BTKeepAlive(byte _version) {
/* 40 */     this.version = _version;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/* 45 */   public String getID() { return "BT_KEEP_ALIVE"; }
/* 46 */   public byte[] getIDBytes() { return BTMessage.ID_BT_KEEP_ALIVE_BYTES; }
/*    */   
/* 48 */   public String getFeatureID() { return "BT1"; }
/*    */   
/* 50 */   public int getFeatureSubID() { return 11; }
/*    */   
/* 52 */   public int getType() { return 0; }
/*    */   
/* 54 */   public byte getVersion() { return this.version; }
/*    */   
/* 56 */   public String getDescription() { return "BT_KEEP_ALIVE"; }
/*    */   
/* 58 */   public DirectByteBuffer[] getData() { return new DirectByteBuffer[0]; }
/*    */   
/*    */   public Message deserialize(DirectByteBuffer data, byte version) throws MessageException {
/* 61 */     if ((data != null) && (data.hasRemaining((byte)11))) {
/* 62 */       throw new MessageException("[" + getID() + "] decode error: payload not empty");
/*    */     }
/*    */     
/* 65 */     if (data != null) { data.returnToPool();
/*    */     }
/* 67 */     return new BTKeepAlive(version);
/*    */   }
/*    */   
/*    */ 
/*    */   public DirectByteBuffer[] getRawData()
/*    */   {
/* 73 */     if (this.buffer == null) {
/* 74 */       DirectByteBuffer dbb = DirectByteBufferPool.getBuffer((byte)20, 4);
/* 75 */       dbb.putInt((byte)6, 0);
/* 76 */       dbb.flip((byte)6);
/* 77 */       this.buffer = new DirectByteBuffer[] { dbb };
/*    */     }
/*    */     
/* 80 */     return this.buffer;
/*    */   }
/*    */   
/* 83 */   public int getPriority() { return 0; }
/*    */   
/* 85 */   public boolean isNoDelay() { return this.no_delay; }
/*    */   
/* 87 */   public void setNoDelay() { this.no_delay = true; }
/*    */   
/* 89 */   public Message[] messagesToRemove() { return null; }
/*    */   
/*    */   public void destroy() {
/* 92 */     if (this.buffer != null)
/* 93 */       this.buffer[0].returnToPool();
/*    */   }
/*    */   
/*    */   public Message getBaseMessage() {
/* 97 */     return this;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/BTKeepAlive.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */