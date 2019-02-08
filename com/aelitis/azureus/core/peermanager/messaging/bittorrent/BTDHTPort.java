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
/*    */ 
/*    */ public class BTDHTPort
/*    */   implements BTMessage
/*    */ {
/*    */   private final int port;
/*    */   private DirectByteBuffer buffer;
/*    */   
/*    */   public BTDHTPort(int port)
/*    */   {
/* 36 */     this.port = port;
/*    */   }
/*    */   
/*    */   public Message deserialize(DirectByteBuffer data, byte version) throws MessageException {
/* 40 */     if (data == null)
/* 41 */       throw new MessageException("[" + getID() + "] decode error: data == null");
/* 42 */     if (data.remaining((byte)11) != 2)
/* 43 */       throw new MessageException("[" + getID() + "] decode error: payload.remaining[" + data.remaining((byte)11) + "] != 2");
/* 44 */     short s_port = data.getShort((byte)11);
/* 45 */     data.returnToPool();
/* 46 */     return new BTDHTPort(0xFFFF & s_port);
/*    */   }
/*    */   
/*    */   public DirectByteBuffer[] getData() {
/* 50 */     if (this.buffer == null) {
/* 51 */       this.buffer = DirectByteBufferPool.getBuffer((byte)30, 2);
/* 52 */       short s_port = (short)this.port;
/* 53 */       this.buffer.put((byte)11, (byte)(s_port >> 8));
/* 54 */       this.buffer.put((byte)11, (byte)(s_port & 0xFF));
/* 55 */       this.buffer.flip((byte)11);
/*    */     }
/* 57 */     return new DirectByteBuffer[] { this.buffer };
/*    */   }
/*    */   
/*    */   public String getDescription() {
/* 61 */     return getID() + " (port " + this.port + ")";
/*    */   }
/*    */   
/*    */   public void destroy() {
/* 65 */     if (this.buffer != null) this.buffer.returnToPool();
/*    */   }
/*    */   
/* 68 */   public String getFeatureID() { return "BT1"; }
/* 69 */   public int getFeatureSubID() { return 9; }
/* 70 */   public String getID() { return "BT_DHT_PORT"; }
/* 71 */   public byte[] getIDBytes() { return BTMessage.ID_BT_DHT_PORT_BYTES; }
/* 72 */   public int getType() { return 0; }
/* 73 */   public byte getVersion() { return 1; }
/*    */   
/* 75 */   public int getDHTPort() { return this.port; }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/BTDHTPort.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */