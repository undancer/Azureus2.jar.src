/*     */ package com.aelitis.azureus.core.peermanager.messaging.azureus;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.RawMessage;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.RawMessageImpl;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageException;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageManager;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTChoke;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTInterested;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTUnchoke;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTUninterested;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBufferPool;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AZMessageFactory
/*     */ {
/*     */   public static final byte MESSAGE_VERSION_INITIAL = 1;
/*     */   public static final byte MESSAGE_VERSION_SUPPORTS_PADDING = 2;
/*     */   public static final int AZ_HANDSHAKE_PAD_MAX = 64;
/*     */   public static final int SMALL_PAD_MAX = 8;
/*     */   public static final int BIG_PAD_MAX = 20;
/*     */   private static final byte bss = 11;
/*  55 */   private static final Map<String, LegacyData> legacy_data = new HashMap();
/*     */   
/*  57 */   static { legacy_data.put("BT_CHOKE", new LegacyData(2, true, new Message[] { new BTUnchoke(0) }));
/*  58 */     legacy_data.put("BT_UNCHOKE", new LegacyData(1, true, new Message[] { new BTChoke(0) }));
/*  59 */     legacy_data.put("BT_INTERESTED", new LegacyData(2, true, new Message[] { new BTUninterested(0) }));
/*  60 */     legacy_data.put("BT_UNINTERESTED", new LegacyData(1, false, new Message[] { new BTInterested(0) }));
/*  61 */     legacy_data.put("BT_HAVE", new LegacyData(0, false, null));
/*  62 */     legacy_data.put("BT_BITFIELD", new LegacyData(2, true, null));
/*  63 */     legacy_data.put("BT_HAVE_ALL", new LegacyData(2, true, null));
/*  64 */     legacy_data.put("BT_HAVE_NONE", new LegacyData(2, true, null));
/*  65 */     legacy_data.put("BT_REQUEST", new LegacyData(1, true, null));
/*  66 */     legacy_data.put("BT_REJECT_REQUEST", new LegacyData(1, true, null));
/*  67 */     legacy_data.put("BT_PIECE", new LegacyData(0, false, null));
/*  68 */     legacy_data.put("BT_CANCEL", new LegacyData(2, true, null));
/*  69 */     legacy_data.put("BT_HANDSHAKE", new LegacyData(2, true, null));
/*  70 */     legacy_data.put("BT_KEEP_ALIVE", new LegacyData(0, false, null));
/*  71 */     legacy_data.put("BT_DHT_PORT", new LegacyData(0, false, null));
/*  72 */     legacy_data.put("BT_SUGGEST_PIECE", new LegacyData(1, true, null));
/*  73 */     legacy_data.put("BT_ALLOWED_FAST", new LegacyData(0, false, null));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void init()
/*     */   {
/*     */     try
/*     */     {
/*  83 */       MessageManager.getSingleton().registerMessageType(new AZHandshake(new byte[20], null, null, "", "", 0, 0, 0, null, 0, new String[0], new byte[0], 0, (byte)2, false));
/*  84 */       MessageManager.getSingleton().registerMessageType(new AZPeerExchange(new byte[20], null, null, (byte)2));
/*  85 */       MessageManager.getSingleton().registerMessageType(new AZRequestHint(-1, -1, -1, -1, (byte)2));
/*  86 */       MessageManager.getSingleton().registerMessageType(new AZHave(new int[0], (byte)2));
/*  87 */       MessageManager.getSingleton().registerMessageType(new AZBadPiece(-1, (byte)2));
/*  88 */       MessageManager.getSingleton().registerMessageType(new AZStatRequest(null, (byte)2));
/*  89 */       MessageManager.getSingleton().registerMessageType(new AZStatReply(null, (byte)2));
/*  90 */       MessageManager.getSingleton().registerMessageType(new AZMetaData(null, null, (byte)2));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (MessageException me)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 103 */       me.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void registerGenericMapPayloadMessageType(String type_id)
/*     */     throws MessageException
/*     */   {
/* 113 */     MessageManager.getSingleton().registerMessageType(new AZGenericMapPayload(type_id, null, (byte)1));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Message createAZMessage(DirectByteBuffer stream_payload)
/*     */     throws MessageException
/*     */   {
/* 126 */     int id_length = stream_payload.getInt((byte)11);
/*     */     
/* 128 */     if ((id_length < 1) || (id_length > 1024) || (id_length > stream_payload.remaining((byte)11) - 1)) {
/* 129 */       byte bt_id = stream_payload.get((byte)0, 0);
/* 130 */       throw new MessageException("invalid AZ id length given: " + id_length + ", stream_payload.remaining(): " + stream_payload.remaining((byte)11) + ", BT id?=" + bt_id);
/*     */     }
/*     */     
/* 133 */     byte[] id_bytes = new byte[id_length];
/*     */     
/* 135 */     stream_payload.get((byte)11, id_bytes);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 142 */     byte version_and_flags = stream_payload.get((byte)11);
/*     */     
/* 144 */     byte version = (byte)(version_and_flags & 0xF);
/*     */     
/* 146 */     if (version >= 2)
/*     */     {
/* 148 */       byte flags = (byte)(version_and_flags >> 4 & 0xF);
/*     */       
/* 150 */       if ((flags & 0x1) != 0)
/*     */       {
/* 152 */         short padding_length = stream_payload.getShort((byte)11);
/*     */         
/* 154 */         byte[] padding = new byte[padding_length];
/*     */         
/* 156 */         stream_payload.get((byte)11, padding);
/*     */       }
/*     */     }
/*     */     
/* 160 */     return MessageManager.getSingleton().createMessage(id_bytes, stream_payload, version);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static RawMessage createAZRawMessage(Message base_message, int padding_mode)
/*     */   {
/* 172 */     byte[] id_bytes = base_message.getIDBytes();
/* 173 */     byte version = base_message.getVersion();
/*     */     
/* 175 */     DirectByteBuffer[] payload = base_message.getData();
/*     */     
/* 177 */     int payload_size = 0;
/* 178 */     for (int i = 0; i < payload.length; i++) {
/* 179 */       payload_size += payload[i].remaining((byte)11);
/*     */     }
/*     */     
/*     */ 
/*     */     DirectByteBuffer header;
/*     */     
/*     */ 
/* 186 */     if (version >= 2)
/*     */     {
/* 188 */       boolean enable_padding = padding_mode != 0;
/*     */       
/*     */       short padding_length;
/*     */       
/* 192 */       if (enable_padding) { short padding_length;
/*     */         short padding_length;
/* 194 */         if (padding_mode == 2)
/*     */         {
/* 196 */           padding_length = (short)RandomUtils.nextInt(8);
/*     */         }
/*     */         else
/*     */         {
/* 200 */           padding_length = (short)RandomUtils.nextInt(payload_size > 256 ? 8 : 20);
/*     */         }
/*     */         
/* 203 */         if (padding_length == 0)
/*     */         {
/* 205 */           enable_padding = false;
/*     */         }
/*     */       }
/*     */       else {
/* 209 */         padding_length = 0;
/*     */       }
/*     */       
/* 212 */       byte flags = enable_padding ? 1 : 0;
/*     */       
/* 214 */       int header_size = 8 + id_bytes.length + 1 + (enable_padding ? 2 + padding_length : 0);
/*     */       
/* 216 */       DirectByteBuffer header = DirectByteBufferPool.getBuffer((byte)22, header_size);
/*     */       
/* 218 */       header.putInt((byte)11, header_size - 4 + payload_size);
/* 219 */       header.putInt((byte)11, id_bytes.length);
/* 220 */       header.put((byte)11, id_bytes);
/*     */       
/* 222 */       byte version_and_flags = (byte)(flags << 4 | version);
/*     */       
/* 224 */       header.put((byte)11, version_and_flags);
/*     */       
/* 226 */       if (enable_padding)
/*     */       {
/* 228 */         byte[] padding = new byte[padding_length];
/*     */         
/* 230 */         header.putShort((byte)11, padding_length);
/* 231 */         header.put((byte)11, padding);
/*     */       }
/*     */     }
/*     */     else {
/* 235 */       int header_size = 8 + id_bytes.length + 1;
/*     */       
/* 237 */       header = DirectByteBufferPool.getBuffer((byte)22, header_size);
/*     */       
/* 239 */       header.putInt((byte)11, header_size - 4 + payload_size);
/* 240 */       header.putInt((byte)11, id_bytes.length);
/* 241 */       header.put((byte)11, id_bytes);
/* 242 */       header.put((byte)11, version);
/*     */     }
/*     */     
/* 245 */     header.flip((byte)11);
/*     */     
/* 247 */     DirectByteBuffer[] raw_buffs = new DirectByteBuffer[payload.length + 1];
/* 248 */     raw_buffs[0] = header;
/* 249 */     System.arraycopy(payload, 0, raw_buffs, 1, payload.length);
/*     */     
/* 251 */     String message_id = base_message.getID();
/*     */     
/* 253 */     LegacyData ld = (LegacyData)legacy_data.get(message_id);
/*     */     
/* 255 */     if (ld != null) {
/* 256 */       return new RawMessageImpl(base_message, raw_buffs, ld.priority, ld.is_no_delay, ld.to_remove);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 262 */     boolean no_delay = true;
/*     */     int priority;
/* 264 */     int priority; if (message_id == "AZ_HANDSHAKE")
/*     */     {
/*     */ 
/*     */ 
/* 268 */       priority = 2;
/*     */     }
/* 270 */     else if (message_id == "AZ_HAVE")
/*     */     {
/* 272 */       int priority = 0;
/* 273 */       no_delay = false;
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/* 279 */       priority = base_message.getType() == 1 ? 0 : 1;
/*     */     }
/*     */     
/* 282 */     return new RawMessageImpl(base_message, raw_buffs, priority, no_delay, null);
/*     */   }
/*     */   
/*     */ 
/*     */   protected static class LegacyData
/*     */   {
/*     */     protected final int priority;
/*     */     
/*     */     protected final boolean is_no_delay;
/*     */     
/*     */     protected final Message[] to_remove;
/*     */     
/*     */     protected LegacyData(int prio, boolean no_delay, Message[] remove)
/*     */     {
/* 296 */       this.priority = prio;
/* 297 */       this.is_no_delay = no_delay;
/* 298 */       this.to_remove = remove;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/azureus/AZMessageFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */