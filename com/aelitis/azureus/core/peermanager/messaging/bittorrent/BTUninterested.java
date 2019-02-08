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
/*    */ public class BTUninterested
/*    */   implements BTMessage
/*    */ {
/*    */   private final byte version;
/*    */   
/*    */   public BTUninterested(byte _version)
/*    */   {
/* 36 */     this.version = _version;
/*    */   }
/*    */   
/* 39 */   public String getID() { return "BT_UNINTERESTED"; }
/* 40 */   public byte[] getIDBytes() { return BTMessage.ID_BT_UNINTERESTED_BYTES; }
/*    */   
/* 42 */   public String getFeatureID() { return "BT1"; }
/*    */   
/* 44 */   public int getFeatureSubID() { return 3; }
/*    */   
/* 46 */   public int getType() { return 0; }
/*    */   
/* 48 */   public byte getVersion() { return this.version; }
/*    */   
/* 50 */   public String getDescription() { return "BT_UNINTERESTED"; }
/*    */   
/* 52 */   public DirectByteBuffer[] getData() { return new DirectByteBuffer[0]; }
/*    */   
/*    */   public Message deserialize(DirectByteBuffer data, byte version) throws MessageException {
/* 55 */     if ((data != null) && (data.hasRemaining((byte)11))) {
/* 56 */       throw new MessageException("[" + getID() + "] decode error: payload not empty");
/*    */     }
/*    */     
/* 59 */     if (data != null) { data.returnToPool();
/*    */     }
/* 61 */     return new BTUninterested(version);
/*    */   }
/*    */   
/*    */   public void destroy() {}
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/BTUninterested.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */