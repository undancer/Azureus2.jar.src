/*    */ package com.aelitis.azureus.core.peermanager.messaging.bittorrent;
/*    */ 
/*    */ import com.aelitis.azureus.core.peermanager.messaging.Message;
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
/*    */ public abstract interface BTMessage
/*    */   extends Message
/*    */ {
/*    */   public static final String BT_FEATURE_ID = "BT1";
/*    */   public static final String ID_BT_CHOKE = "BT_CHOKE";
/* 31 */   public static final byte[] ID_BT_CHOKE_BYTES = "BT_CHOKE".getBytes();
/*    */   
/*    */   public static final int SUBID_BT_CHOKE = 0;
/*    */   public static final String ID_BT_UNCHOKE = "BT_UNCHOKE";
/* 35 */   public static final byte[] ID_BT_UNCHOKE_BYTES = "BT_UNCHOKE".getBytes();
/*    */   
/*    */   public static final int SUBID_BT_UNCHOKE = 1;
/*    */   public static final String ID_BT_INTERESTED = "BT_INTERESTED";
/* 39 */   public static final byte[] ID_BT_INTERESTED_BYTES = "BT_INTERESTED".getBytes();
/*    */   
/*    */   public static final int SUBID_BT_INTERESTED = 2;
/*    */   public static final String ID_BT_UNINTERESTED = "BT_UNINTERESTED";
/* 43 */   public static final byte[] ID_BT_UNINTERESTED_BYTES = "BT_UNINTERESTED".getBytes();
/*    */   
/*    */   public static final int SUBID_BT_UNINTERESTED = 3;
/*    */   public static final String ID_BT_HAVE = "BT_HAVE";
/* 47 */   public static final byte[] ID_BT_HAVE_BYTES = "BT_HAVE".getBytes();
/*    */   
/*    */   public static final int SUBID_BT_HAVE = 4;
/*    */   public static final String ID_BT_BITFIELD = "BT_BITFIELD";
/* 51 */   public static final byte[] ID_BT_BITFIELD_BYTES = "BT_BITFIELD".getBytes();
/*    */   
/*    */   public static final int SUBID_BT_BITFIELD = 5;
/*    */   public static final String ID_BT_REQUEST = "BT_REQUEST";
/* 55 */   public static final byte[] ID_BT_REQUEST_BYTES = "BT_REQUEST".getBytes();
/*    */   
/*    */   public static final int SUBID_BT_REQUEST = 6;
/*    */   public static final String ID_BT_PIECE = "BT_PIECE";
/* 59 */   public static final byte[] ID_BT_PIECE_BYTES = "BT_PIECE".getBytes();
/*    */   
/*    */   public static final int SUBID_BT_PIECE = 7;
/*    */   public static final String ID_BT_CANCEL = "BT_CANCEL";
/* 63 */   public static final byte[] ID_BT_CANCEL_BYTES = "BT_CANCEL".getBytes();
/*    */   
/*    */   public static final int SUBID_BT_CANCEL = 8;
/*    */   public static final String ID_BT_DHT_PORT = "BT_DHT_PORT";
/* 67 */   public static final byte[] ID_BT_DHT_PORT_BYTES = "BT_DHT_PORT".getBytes();
/*    */   
/*    */   public static final int SUBID_BT_DHT_PORT = 9;
/*    */   public static final String ID_BT_HANDSHAKE = "BT_HANDSHAKE";
/* 71 */   public static final byte[] ID_BT_HANDSHAKE_BYTES = "BT_HANDSHAKE".getBytes();
/*    */   
/*    */   public static final int SUBID_BT_HANDSHAKE = 10;
/*    */   public static final String ID_BT_KEEP_ALIVE = "BT_KEEP_ALIVE";
/* 75 */   public static final byte[] ID_BT_KEEP_ALIVE_BYTES = "BT_KEEP_ALIVE".getBytes();
/*    */   
/*    */   public static final int SUBID_BT_KEEP_ALIVE = 11;
/*    */   public static final String ID_BT_SUGGEST_PIECE = "BT_SUGGEST_PIECE";
/* 79 */   public static final byte[] ID_BT_SUGGEST_PIECE_BYTES = "BT_SUGGEST_PIECE".getBytes();
/*    */   
/*    */   public static final int SUBID_BT_SUGGEST_PIECE = 13;
/*    */   public static final String ID_BT_HAVE_ALL = "BT_HAVE_ALL";
/* 83 */   public static final byte[] ID_BT_HAVE_ALL_BYTES = "BT_HAVE_ALL".getBytes();
/*    */   
/*    */   public static final int SUBID_BT_HAVE_ALL = 14;
/*    */   public static final String ID_BT_HAVE_NONE = "BT_HAVE_NONE";
/* 87 */   public static final byte[] ID_BT_HAVE_NONE_BYTES = "BT_HAVE_NONE".getBytes();
/*    */   
/*    */   public static final int SUBID_BT_HAVE_NONE = 15;
/*    */   public static final String ID_BT_REJECT_REQUEST = "BT_REJECT_REQUEST";
/* 91 */   public static final byte[] ID_BT_REJECT_REQUEST_BYTES = "BT_REJECT_REQUEST".getBytes();
/*    */   
/*    */   public static final int SUBID_BT_REJECT_REQUEST = 16;
/*    */   public static final String ID_BT_ALLOWED_FAST = "BT_ALLOWED_FAST";
/* 95 */   public static final byte[] ID_BT_ALLOWED_FAST_BYTES = "BT_ALLOWED_FAST".getBytes();
/*    */   
/*    */   public static final int SUBID_BT_ALLOWED_FAST = 17;
/*    */   public static final String ID_BT_LT_EXT_MESSAGE = "BT_LT_EXT_MESSAGE";
/* 99 */   public static final byte[] ID_BT_LT_EXT_MESSAGE_BYTES = "BT_LT_EXT_MESSAGE".getBytes();
/*    */   public static final int SUBID_BT_LT_EXT_MESSAGE = 20;
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/BTMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */