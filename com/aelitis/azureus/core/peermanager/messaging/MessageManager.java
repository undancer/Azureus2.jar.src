/*     */ package com.aelitis.azureus.core.peermanager.messaging;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.messaging.azureus.AZMessageFactory;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTMessageFactory;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.ltep.LTMessageFactory;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.ByteArrayHashMap;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MessageManager
/*     */ {
/*  38 */   private static final MessageManager instance = new MessageManager();
/*     */   
/*  40 */   private final ByteArrayHashMap message_map = new ByteArrayHashMap();
/*  41 */   private final List messages = new ArrayList();
/*     */   
/*  43 */   protected final AEMonitor this_mon = new AEMonitor("MessageManager");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static MessageManager getSingleton()
/*     */   {
/*  50 */     return instance;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void initialize()
/*     */   {
/*  57 */     AZMessageFactory.init();
/*  58 */     BTMessageFactory.init();
/*  59 */     LTMessageFactory.init();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void registerMessageType(Message message)
/*     */     throws MessageException
/*     */   {
/*     */     try
/*     */     {
/*  72 */       this.this_mon.enter();
/*     */       
/*  74 */       byte[] id_bytes = message.getIDBytes();
/*     */       
/*  76 */       if (this.message_map.containsKey(id_bytes)) {
/*  77 */         throw new MessageException("message type [" + message.getID() + "] already registered!");
/*     */       }
/*     */       
/*  80 */       this.message_map.put(id_bytes, message);
/*     */       
/*  82 */       this.messages.add(message);
/*     */     }
/*     */     finally {
/*  85 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void deregisterMessageType(Message message)
/*     */   {
/*     */     try
/*     */     {
/*  96 */       this.this_mon.enter();
/*     */       
/*  98 */       this.message_map.remove(message.getIDBytes());
/*     */       
/* 100 */       this.messages.remove(message);
/*     */     } finally {
/* 102 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Message createMessage(byte[] id_bytes, DirectByteBuffer message_data, byte version)
/*     */     throws MessageException
/*     */   {
/* 114 */     Message message = (Message)this.message_map.get(id_bytes);
/*     */     
/* 116 */     if (message == null) {
/* 117 */       throw new MessageException("message id[" + new String(id_bytes) + "] not registered");
/*     */     }
/*     */     
/* 120 */     return message.deserialize(message_data, version);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Message lookupMessage(String id)
/*     */   {
/* 131 */     return (Message)this.message_map.get(id.getBytes());
/*     */   }
/*     */   
/*     */   public Message lookupMessage(byte[] id_bytes)
/*     */   {
/* 136 */     return (Message)this.message_map.get(id_bytes);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Message[] getRegisteredMessages()
/*     */   {
/* 145 */     return (Message[])this.messages.toArray(new Message[this.messages.size()]);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/MessageManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */