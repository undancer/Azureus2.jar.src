/*     */ package com.aelitis.azureus.core.peermanager.messaging.bittorrent;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.RawMessage;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.RawMessageImpl;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageException;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageManager;
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBufferPool;
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
/*     */ public class BTMessageFactory
/*     */ {
/*     */   public static final byte MESSAGE_VERSION_INITIAL = 1;
/*     */   public static final byte MESSAGE_VERSION_SUPPORTS_PADDING = 2;
/*  41 */   private static final LogIDs LOGID = LogIDs.PEER;
/*     */   
/*     */ 
/*     */   public static void init()
/*     */   {
/*     */     try
/*     */     {
/*  48 */       MessageManager.getSingleton().registerMessageType(new BTBitfield(null, (byte)2));
/*  49 */       MessageManager.getSingleton().registerMessageType(new BTCancel(-1, -1, -1, (byte)2));
/*  50 */       MessageManager.getSingleton().registerMessageType(new BTChoke((byte)2));
/*  51 */       MessageManager.getSingleton().registerMessageType(new BTHandshake(new byte[0], new byte[0], 2, (byte)1));
/*  52 */       MessageManager.getSingleton().registerMessageType(new BTHave(-1, (byte)2));
/*  53 */       MessageManager.getSingleton().registerMessageType(new BTInterested((byte)2));
/*  54 */       MessageManager.getSingleton().registerMessageType(new BTKeepAlive((byte)2));
/*  55 */       MessageManager.getSingleton().registerMessageType(new BTPiece(-1, -1, null, (byte)2));
/*  56 */       MessageManager.getSingleton().registerMessageType(new BTRequest(-1, -1, -1, (byte)2));
/*  57 */       MessageManager.getSingleton().registerMessageType(new BTUnchoke((byte)2));
/*  58 */       MessageManager.getSingleton().registerMessageType(new BTUninterested((byte)2));
/*  59 */       MessageManager.getSingleton().registerMessageType(new BTSuggestPiece(-1, (byte)2));
/*  60 */       MessageManager.getSingleton().registerMessageType(new BTHaveAll((byte)2));
/*  61 */       MessageManager.getSingleton().registerMessageType(new BTHaveNone((byte)2));
/*  62 */       MessageManager.getSingleton().registerMessageType(new BTRejectRequest(-1, -1, -1, (byte)2));
/*  63 */       MessageManager.getSingleton().registerMessageType(new BTAllowedFast(-1, (byte)2));
/*  64 */       MessageManager.getSingleton().registerMessageType(new BTLTMessage(null, (byte)2));
/*  65 */       MessageManager.getSingleton().registerMessageType(new BTDHTPort(-1));
/*     */     } catch (MessageException me) {
/*  67 */       me.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  73 */   private static final String[] id_to_name = new String[21];
/*  74 */   private static final HashMap legacy_data = new HashMap();
/*     */   
/*  76 */   static { legacy_data.put("BT_CHOKE", new LegacyData(2, true, new Message[] { new BTUnchoke(0), new BTPiece(-1, -1, null, 0) }, (byte)0));
/*  77 */     id_to_name[0] = "BT_CHOKE";
/*     */     
/*  79 */     legacy_data.put("BT_UNCHOKE", new LegacyData(1, true, new Message[] { new BTChoke(0) }, (byte)1));
/*  80 */     id_to_name[1] = "BT_UNCHOKE";
/*     */     
/*  82 */     legacy_data.put("BT_INTERESTED", new LegacyData(2, true, new Message[] { new BTUninterested(0) }, (byte)2));
/*  83 */     id_to_name[2] = "BT_INTERESTED";
/*     */     
/*  85 */     legacy_data.put("BT_UNINTERESTED", new LegacyData(1, false, new Message[] { new BTInterested(0) }, (byte)3));
/*  86 */     id_to_name[3] = "BT_UNINTERESTED";
/*     */     
/*  88 */     legacy_data.put("BT_HAVE", new LegacyData(0, false, null, (byte)4));
/*  89 */     id_to_name[4] = "BT_HAVE";
/*     */     
/*  91 */     legacy_data.put("BT_BITFIELD", new LegacyData(2, true, null, (byte)5));
/*  92 */     id_to_name[5] = "BT_BITFIELD";
/*     */     
/*  94 */     legacy_data.put("BT_REQUEST", new LegacyData(1, true, null, (byte)6));
/*  95 */     id_to_name[6] = "BT_REQUEST";
/*     */     
/*  97 */     legacy_data.put("BT_PIECE", new LegacyData(0, false, null, (byte)7));
/*  98 */     id_to_name[7] = "BT_PIECE";
/*     */     
/* 100 */     legacy_data.put("BT_CANCEL", new LegacyData(2, true, null, (byte)8));
/* 101 */     id_to_name[8] = "BT_CANCEL";
/*     */     
/* 103 */     legacy_data.put("BT_DHT_PORT", new LegacyData(0, true, null, (byte)9));
/* 104 */     id_to_name[9] = "BT_DHT_PORT";
/*     */     
/* 106 */     legacy_data.put("BT_SUGGEST_PIECE", new LegacyData(1, true, null, (byte)13));
/* 107 */     id_to_name[13] = "BT_SUGGEST_PIECE";
/*     */     
/* 109 */     legacy_data.put("BT_HAVE_ALL", new LegacyData(2, true, null, (byte)14));
/* 110 */     id_to_name[14] = "BT_HAVE_ALL";
/*     */     
/* 112 */     legacy_data.put("BT_HAVE_NONE", new LegacyData(2, true, null, (byte)15));
/* 113 */     id_to_name[15] = "BT_HAVE_NONE";
/*     */     
/* 115 */     legacy_data.put("BT_REJECT_REQUEST", new LegacyData(1, true, null, (byte)16));
/* 116 */     id_to_name[16] = "BT_REJECT_REQUEST";
/*     */     
/* 118 */     legacy_data.put("BT_ALLOWED_FAST", new LegacyData(0, false, null, (byte)17));
/* 119 */     id_to_name[17] = "BT_ALLOWED_FAST";
/*     */     
/* 121 */     legacy_data.put("BT_LT_EXT_MESSAGE", new LegacyData(2, true, null, (byte)20));
/* 122 */     id_to_name[20] = "BT_LT_EXT_MESSAGE";
/*     */   }
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
/*     */   public static Message createBTMessage(DirectByteBuffer stream_payload)
/*     */     throws MessageException
/*     */   {
/* 138 */     byte id = stream_payload.get((byte)11);
/*     */     
/* 140 */     switch (id) {
/*     */     case 0: 
/* 142 */       return MessageManager.getSingleton().createMessage(BTMessage.ID_BT_CHOKE_BYTES, stream_payload, (byte)1);
/*     */     
/*     */     case 1: 
/* 145 */       return MessageManager.getSingleton().createMessage(BTMessage.ID_BT_UNCHOKE_BYTES, stream_payload, (byte)1);
/*     */     
/*     */     case 2: 
/* 148 */       return MessageManager.getSingleton().createMessage(BTMessage.ID_BT_INTERESTED_BYTES, stream_payload, (byte)1);
/*     */     
/*     */     case 3: 
/* 151 */       return MessageManager.getSingleton().createMessage(BTMessage.ID_BT_UNINTERESTED_BYTES, stream_payload, (byte)1);
/*     */     
/*     */     case 4: 
/* 154 */       return MessageManager.getSingleton().createMessage(BTMessage.ID_BT_HAVE_BYTES, stream_payload, (byte)1);
/*     */     
/*     */     case 5: 
/* 157 */       return MessageManager.getSingleton().createMessage(BTMessage.ID_BT_BITFIELD_BYTES, stream_payload, (byte)1);
/*     */     
/*     */     case 6: 
/* 160 */       return MessageManager.getSingleton().createMessage(BTMessage.ID_BT_REQUEST_BYTES, stream_payload, (byte)1);
/*     */     
/*     */     case 7: 
/* 163 */       return MessageManager.getSingleton().createMessage(BTMessage.ID_BT_PIECE_BYTES, stream_payload, (byte)1);
/*     */     
/*     */     case 8: 
/* 166 */       return MessageManager.getSingleton().createMessage(BTMessage.ID_BT_CANCEL_BYTES, stream_payload, (byte)1);
/*     */     
/*     */     case 9: 
/* 169 */       return MessageManager.getSingleton().createMessage(BTMessage.ID_BT_DHT_PORT_BYTES, stream_payload, (byte)1);
/*     */     
/*     */     case 13: 
/* 172 */       return MessageManager.getSingleton().createMessage(BTMessage.ID_BT_SUGGEST_PIECE_BYTES, stream_payload, (byte)1);
/*     */     
/*     */     case 14: 
/* 175 */       return MessageManager.getSingleton().createMessage(BTMessage.ID_BT_HAVE_ALL_BYTES, stream_payload, (byte)1);
/*     */     
/*     */     case 15: 
/* 178 */       return MessageManager.getSingleton().createMessage(BTMessage.ID_BT_HAVE_NONE_BYTES, stream_payload, (byte)1);
/*     */     
/*     */     case 16: 
/* 181 */       return MessageManager.getSingleton().createMessage(BTMessage.ID_BT_REJECT_REQUEST_BYTES, stream_payload, (byte)1);
/*     */     
/*     */     case 17: 
/* 184 */       return MessageManager.getSingleton().createMessage(BTMessage.ID_BT_ALLOWED_FAST_BYTES, stream_payload, (byte)1);
/*     */     
/*     */ 
/*     */ 
/*     */     case 20: 
/* 189 */       if (Logger.isEnabled()) {
/* 190 */         Logger.log(new LogEvent(LOGID, 1, "Old extended messaging hello received (or malformed LT extension message), ignoring and faking as keep-alive."));
/*     */       }
/*     */       
/* 193 */       return MessageManager.getSingleton().createMessage(BTMessage.ID_BT_KEEP_ALIVE_BYTES, null, (byte)1);
/*     */     }
/* 195 */     System.out.println("Unknown BT message id [" + id + "]");
/* 196 */     throw new MessageException("Unknown BT message id [" + id + "]");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int getMessageType(DirectByteBuffer stream_payload)
/*     */   {
/* 204 */     byte id = stream_payload.get((byte)11, 0);
/* 205 */     if (id == 84) return 0;
/* 206 */     if ((id >= 0) && (id < id_to_name.length)) {
/* 207 */       String name = id_to_name[id];
/*     */       
/* 209 */       if (name != null)
/*     */       {
/* 211 */         Message message = MessageManager.getSingleton().lookupMessage(name);
/*     */         
/* 213 */         if (message != null)
/*     */         {
/* 215 */           return message.getType();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 220 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static RawMessage createBTRawMessage(Message base_message)
/*     */   {
/* 231 */     if ((base_message instanceof RawMessage)) {
/* 232 */       return (RawMessage)base_message;
/*     */     }
/*     */     
/* 235 */     LegacyData ld = (LegacyData)legacy_data.get(base_message.getID());
/*     */     
/* 237 */     if (ld == null) {
/* 238 */       Debug.out("legacy message type id not found for [" + base_message.getID() + "]");
/* 239 */       return null;
/*     */     }
/*     */     
/* 242 */     DirectByteBuffer[] payload = base_message.getData();
/*     */     
/* 244 */     int payload_size = 0;
/* 245 */     for (int i = 0; i < payload.length; i++) {
/* 246 */       payload_size += payload[i].remaining((byte)11);
/*     */     }
/*     */     
/* 249 */     DirectByteBuffer header = DirectByteBufferPool.getBuffer((byte)21, 5);
/* 250 */     header.putInt((byte)11, 1 + payload_size);
/* 251 */     header.put((byte)11, ld.bt_id);
/* 252 */     header.flip((byte)11);
/*     */     
/* 254 */     DirectByteBuffer[] raw_buffs = new DirectByteBuffer[payload.length + 1];
/* 255 */     raw_buffs[0] = header;
/* 256 */     System.arraycopy(payload, 0, raw_buffs, 1, payload.length);
/*     */     
/* 258 */     return new RawMessageImpl(base_message, raw_buffs, ld.priority, ld.is_no_delay, ld.to_remove);
/*     */   }
/*     */   
/*     */ 
/*     */   protected static class LegacyData
/*     */   {
/*     */     protected final int priority;
/*     */     protected final boolean is_no_delay;
/*     */     protected final Message[] to_remove;
/*     */     protected final byte bt_id;
/*     */     
/*     */     protected LegacyData(int prio, boolean no_delay, Message[] remove, byte btid)
/*     */     {
/* 271 */       this.priority = prio;
/* 272 */       this.is_no_delay = no_delay;
/* 273 */       this.to_remove = remove;
/* 274 */       this.bt_id = btid;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/BTMessageFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */