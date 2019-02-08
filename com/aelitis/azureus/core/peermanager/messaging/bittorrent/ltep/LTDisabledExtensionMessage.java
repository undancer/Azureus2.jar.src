/*    */ package com.aelitis.azureus.core.peermanager.messaging.bittorrent.ltep;
/*    */ 
/*    */ import com.aelitis.azureus.core.peermanager.messaging.Message;
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
/*    */ public class LTDisabledExtensionMessage
/*    */   implements LTMessage
/*    */ {
/* 30 */   public static final LTDisabledExtensionMessage INSTANCE = new LTDisabledExtensionMessage();
/*    */   
/*    */ 
/*    */   public Message deserialize(DirectByteBuffer data, byte version)
/*    */   {
/* 35 */     return INSTANCE;
/*    */   }
/*    */   
/*    */   public void destroy() {}
/*    */   
/*    */   public DirectByteBuffer[] getData()
/*    */   {
/* 42 */     throw new RuntimeException("Disabled extension message not meant to be used for serialisation!");
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public String getDescription()
/*    */   {
/* 49 */     return "Disabled extension message over LTEP (ignored)";
/*    */   }
/*    */   
/* 52 */   public String getFeatureID() { return "LT1"; }
/* 53 */   public int getFeatureSubID() { return 2; }
/* 54 */   public String getID() { return "disabled_extension"; }
/* 55 */   public byte[] getIDBytes() { return LTMessage.ID_DISABLED_EXT_BYTES; }
/* 56 */   public int getType() { return 0; }
/* 57 */   public byte getVersion() { return 0; }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/ltep/LTDisabledExtensionMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */