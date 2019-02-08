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
/*    */ public class BTHaveAll
/*    */   implements BTMessage
/*    */ {
/*    */   private final byte version;
/*    */   
/*    */   public BTHaveAll(byte _version)
/*    */   {
/* 34 */     this.version = _version;
/*    */   }
/*    */   
/* 37 */   public String getID() { return "BT_HAVE_ALL"; }
/* 38 */   public byte[] getIDBytes() { return BTMessage.ID_BT_HAVE_ALL_BYTES; }
/*    */   
/* 40 */   public String getFeatureID() { return "BT1"; }
/*    */   
/* 42 */   public int getFeatureSubID() { return 14; }
/*    */   
/* 44 */   public int getType() { return 0; }
/*    */   
/* 46 */   public byte getVersion() { return this.version; }
/*    */   
/* 48 */   public String getDescription() { return "BT_HAVE_ALL"; }
/*    */   
/* 50 */   public DirectByteBuffer[] getData() { return new DirectByteBuffer[0]; }
/*    */   
/*    */   public Message deserialize(DirectByteBuffer data, byte version) throws MessageException {
/* 53 */     if ((data != null) && (data.hasRemaining((byte)11))) {
/* 54 */       throw new MessageException("[" + getID() + "] decode error: payload not empty [" + data.remaining((byte)11) + "]");
/*    */     }
/*    */     
/* 57 */     if (data != null) { data.returnToPool();
/*    */     }
/* 59 */     return new BTHaveAll(version);
/*    */   }
/*    */   
/*    */   public void destroy() {}
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/BTHaveAll.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */