/*     */ package com.aelitis.azureus.core.peermanager.messaging.bittorrent.ltep;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageException;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageManager;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTMessageDecoder;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteMap;
/*     */ import java.io.PrintStream;
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
/*     */ public class LTMessageDecoder
/*     */   extends BTMessageDecoder
/*     */ {
/*  34 */   private static final CopyOnWriteMap<Byte, byte[]> default_entension_handlers = new CopyOnWriteMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void addDefaultExtensionHandler(long id, byte[] message_id)
/*     */   {
/*  41 */     default_entension_handlers.put(Byte.valueOf((byte)(int)id), message_id);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void removeDefaultExtensionHandler(long id)
/*     */   {
/*  48 */     default_entension_handlers.remove(Byte.valueOf((byte)(int)id));
/*     */   }
/*     */   
/*  51 */   private final CopyOnWriteMap<Byte, byte[]> extension_handlers = new CopyOnWriteMap();
/*     */   
/*     */ 
/*     */   public LTMessageDecoder()
/*     */   {
/*  56 */     if (default_entension_handlers.size() > 0)
/*     */     {
/*  58 */       this.extension_handlers.putAll(default_entension_handlers);
/*     */     }
/*     */   }
/*     */   
/*     */   protected Message createMessage(DirectByteBuffer ref_buff) throws MessageException
/*     */   {
/*  64 */     int old_position = ref_buff.position((byte)11);
/*  65 */     byte id = ref_buff.get((byte)11);
/*  66 */     if (id != 20) {
/*  67 */       ref_buff.position((byte)11, old_position);
/*  68 */       return super.createMessage(ref_buff);
/*     */     }
/*     */     
/*     */ 
/*  72 */     id = ref_buff.get((byte)11);
/*  73 */     switch (id) {
/*     */     case 0: 
/*  75 */       return MessageManager.getSingleton().createMessage(LTMessage.ID_LT_HANDSHAKE_BYTES, ref_buff, (byte)1);
/*     */     case 1: 
/*  77 */       return MessageManager.getSingleton().createMessage(LTMessage.ID_UT_PEX_BYTES, ref_buff, (byte)1);
/*     */     case 3: 
/*  79 */       return MessageManager.getSingleton().createMessage(LTMessage.ID_UT_METADATA_BYTES, ref_buff, (byte)1);
/*     */     case 4: 
/*  81 */       return MessageManager.getSingleton().createMessage(LTMessage.ID_UT_UPLOAD_ONLY_BYTES, ref_buff, (byte)1);
/*     */     }
/*     */     
/*     */     
/*  85 */     byte[] message_id = (byte[])this.extension_handlers.get(Byte.valueOf(id));
/*     */     
/*  87 */     if (message_id != null) {
/*  88 */       return MessageManager.getSingleton().createMessage(message_id, ref_buff, (byte)1);
/*     */     }
/*  90 */     System.out.println("Unknown LTEP message id [" + id + "]");
/*  91 */     throw new MessageException("Unknown LTEP message id [" + id + "]");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addExtensionHandler(byte id, byte[] message_id)
/*     */   {
/* 101 */     this.extension_handlers.put(Byte.valueOf(id), message_id);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeExtensionHandler(byte id)
/*     */   {
/* 108 */     this.extension_handlers.remove(Byte.valueOf(id));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/ltep/LTMessageDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */