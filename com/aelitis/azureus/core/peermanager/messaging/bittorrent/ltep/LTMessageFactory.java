/*    */ package com.aelitis.azureus.core.peermanager.messaging.bittorrent.ltep;
/*    */ 
/*    */ import com.aelitis.azureus.core.peermanager.messaging.MessageException;
/*    */ import com.aelitis.azureus.core.peermanager.messaging.MessageManager;
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
/*    */ public class LTMessageFactory
/*    */ {
/*    */   public static final byte MESSAGE_VERSION_INITIAL = 1;
/*    */   public static final byte MESSAGE_VERSION_SUPPORTS_PADDING = 2;
/*    */   
/*    */   public static void init()
/*    */   {
/*    */     try
/*    */     {
/* 34 */       MessageManager.getSingleton().registerMessageType(new LTHandshake(null, (byte)2));
/* 35 */       MessageManager.getSingleton().registerMessageType(new UTPeerExchange(null, null, null, (byte)2));
/* 36 */       MessageManager.getSingleton().registerMessageType(new UTMetaData(null, null, (byte)2));
/* 37 */       MessageManager.getSingleton().registerMessageType(new UTUploadOnly(false, (byte)2));
/*    */     } catch (MessageException me) {
/* 39 */       me.printStackTrace();
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/ltep/LTMessageFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */