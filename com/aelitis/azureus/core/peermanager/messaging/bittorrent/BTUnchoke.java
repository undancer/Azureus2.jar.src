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
/*    */ 
/*    */ public class BTUnchoke
/*    */   implements BTMessage
/*    */ {
/*    */   private final byte version;
/*    */   
/*    */   public BTUnchoke(byte _version)
/*    */   {
/* 38 */     this.version = _version;
/*    */   }
/*    */   
/* 41 */   public String getID() { return "BT_UNCHOKE"; }
/* 42 */   public byte[] getIDBytes() { return BTMessage.ID_BT_UNCHOKE_BYTES; }
/*    */   
/* 44 */   public String getFeatureID() { return "BT1"; }
/*    */   
/* 46 */   public int getFeatureSubID() { return 1; }
/*    */   
/* 48 */   public int getType() { return 0; }
/*    */   
/* 50 */   public byte getVersion() { return this.version; }
/*    */   
/* 52 */   public String getDescription() { return "BT_UNCHOKE"; }
/*    */   
/* 54 */   public DirectByteBuffer[] getData() { return new DirectByteBuffer[0]; }
/*    */   
/*    */   public Message deserialize(DirectByteBuffer data, byte version) throws MessageException {
/* 57 */     if ((data != null) && (data.hasRemaining((byte)11))) {
/* 58 */       throw new MessageException("[" + getID() + "] decode error: payload not empty");
/*    */     }
/*    */     
/* 61 */     if (data != null) { data.returnToPool();
/*    */     }
/* 63 */     return new BTUnchoke(version);
/*    */   }
/*    */   
/*    */   public void destroy() {}
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/BTUnchoke.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */