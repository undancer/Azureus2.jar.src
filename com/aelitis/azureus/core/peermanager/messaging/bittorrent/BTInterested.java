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
/*    */ public class BTInterested
/*    */   implements BTMessage
/*    */ {
/*    */   private final byte version;
/*    */   
/*    */   public BTInterested(byte _version)
/*    */   {
/* 37 */     this.version = _version;
/*    */   }
/*    */   
/* 40 */   public String getID() { return "BT_INTERESTED"; }
/* 41 */   public byte[] getIDBytes() { return BTMessage.ID_BT_INTERESTED_BYTES; }
/*    */   
/* 43 */   public String getFeatureID() { return "BT1"; }
/*    */   
/* 45 */   public int getFeatureSubID() { return 2; }
/*    */   
/* 47 */   public int getType() { return 0; }
/*    */   
/* 49 */   public byte getVersion() { return this.version; }
/*    */   
/* 51 */   public String getDescription() { return "BT_INTERESTED"; }
/*    */   
/* 53 */   public DirectByteBuffer[] getData() { return new DirectByteBuffer[0]; }
/*    */   
/*    */   public Message deserialize(DirectByteBuffer data, byte version) throws MessageException {
/* 56 */     if ((data != null) && (data.hasRemaining((byte)11))) {
/* 57 */       throw new MessageException("[" + getID() + "] decode error: payload not empty");
/*    */     }
/*    */     
/* 60 */     if (data != null) { data.returnToPool();
/*    */     }
/* 62 */     return new BTInterested(version);
/*    */   }
/*    */   
/*    */   public void destroy() {}
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/BTInterested.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */