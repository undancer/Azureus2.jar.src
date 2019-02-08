/*    */ package com.aelitis.azureus.core.peermanager.messaging.azureus;
/*    */ 
/*    */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*    */ import com.aelitis.azureus.core.peermanager.messaging.MessageException;
/*    */ import com.aelitis.azureus.core.peermanager.messaging.MessagingUtil;
/*    */ import java.util.Map;
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
/*    */ public class AZGenericMapPayload
/*    */   implements AZMessage
/*    */ {
/*    */   private final byte version;
/* 35 */   private DirectByteBuffer buffer = null;
/*    */   
/*    */ 
/*    */   private final String type_id;
/*    */   
/*    */ 
/*    */   private final Map msg_map;
/*    */   
/*    */ 
/*    */ 
/*    */   public AZGenericMapPayload(String message_type, Map message, byte version)
/*    */   {
/* 47 */     this.type_id = message_type;
/* 48 */     this.msg_map = message;
/* 49 */     this.version = version;
/*    */   }
/*    */   
/*    */ 
/* 53 */   public String getID() { return this.type_id; }
/* 54 */   public byte[] getIDBytes() { return this.type_id.getBytes(); }
/*    */   
/* 56 */   public String getFeatureID() { return "AZ1"; }
/*    */   
/* 58 */   public int getFeatureSubID() { return 2; }
/*    */   
/*    */ 
/* 61 */   public int getType() { return 0; }
/*    */   
/* 63 */   public byte getVersion() { return this.version; }
/*    */   
/* 65 */   public Map getMapPayload() { return this.msg_map; }
/*    */   
/*    */   public String getDescription() {
/* 68 */     return getID();
/*    */   }
/*    */   
/*    */   public DirectByteBuffer[] getData() {
/* 72 */     if (this.buffer == null) {
/* 73 */       this.buffer = MessagingUtil.convertPayloadToBencodedByteStream(this.msg_map, (byte)12);
/*    */     }
/* 75 */     return new DirectByteBuffer[] { this.buffer };
/*    */   }
/*    */   
/*    */   public Message deserialize(DirectByteBuffer data, byte version) throws MessageException
/*    */   {
/* 80 */     Map payload = MessagingUtil.convertBencodedByteStreamToPayload(data, 1, getID());
/* 81 */     return new AZGenericMapPayload(getID(), payload, version);
/*    */   }
/*    */   
/*    */   public void destroy()
/*    */   {
/* 86 */     if (this.buffer != null) this.buffer.returnToPool();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/azureus/AZGenericMapPayload.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */