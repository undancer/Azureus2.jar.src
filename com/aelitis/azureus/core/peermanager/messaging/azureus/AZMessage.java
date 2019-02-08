/*    */ package com.aelitis.azureus.core.peermanager.messaging.azureus;
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
/*    */ public abstract interface AZMessage
/*    */   extends Message
/*    */ {
/*    */   public static final String AZ_FEATURE_ID = "AZ1";
/*    */   public static final String ID_AZ_HANDSHAKE = "AZ_HANDSHAKE";
/* 31 */   public static final byte[] ID_AZ_HANDSHAKE_BYTES = "AZ_HANDSHAKE".getBytes();
/*    */   
/*    */   public static final int SUBID_AZ_HANDSHAKE = 0;
/*    */   public static final String ID_AZ_PEER_EXCHANGE = "AZ_PEER_EXCHANGE";
/* 35 */   public static final byte[] ID_AZ_PEER_EXCHANGE_BYTES = "AZ_PEER_EXCHANGE".getBytes();
/*    */   
/*    */   public static final int SUBID_AZ_PEER_EXCHANGE = 1;
/*    */   public static final String ID_AZ_GENERIC_MAP = "AZ_GENERIC_MAP";
/* 39 */   public static final byte[] ID_AZ_GENERIC_MAP_BYTES = "AZ_GENERIC_MAP".getBytes();
/*    */   
/*    */   public static final int SUBID_AZ_GENERIC_MAP = 2;
/*    */   public static final String ID_AZ_REQUEST_HINT = "AZ_REQUEST_HINT";
/* 43 */   public static final byte[] ID_AZ_REQUEST_HINT_BYTES = "AZ_REQUEST_HINT".getBytes();
/*    */   
/*    */   public static final int SUBID_ID_AZ_REQUEST_HINT = 3;
/*    */   public static final String ID_AZ_HAVE = "AZ_HAVE";
/* 47 */   public static final byte[] ID_AZ_HAVE_BYTES = "AZ_HAVE".getBytes();
/*    */   
/*    */   public static final int SUBID_ID_AZ_HAVE = 4;
/*    */   public static final String ID_AZ_BAD_PIECE = "AZ_BAD_PIECE";
/* 51 */   public static final byte[] ID_AZ_BAD_PIECE_BYTES = "AZ_BAD_PIECE".getBytes();
/*    */   
/*    */   public static final int SUBID_ID_AZ_BAD_PIECE = 5;
/*    */   public static final String ID_AZ_STAT_REQUEST = "AZ_STAT_REQ";
/* 55 */   public static final byte[] ID_AZ_STAT_REQUEST_BYTES = "AZ_STAT_REQ".getBytes();
/*    */   
/*    */   public static final int SUBID_ID_AZ_STAT_REQUEST = 6;
/*    */   public static final String ID_AZ_STAT_REPLY = "AZ_STAT_REP";
/* 59 */   public static final byte[] ID_AZ_STAT_REPLY_BYTES = "AZ_STAT_REP".getBytes();
/*    */   
/*    */   public static final int SUBID_ID_AZ_STAT_REPLY = 7;
/*    */   public static final String ID_AZ_METADATA = "AZ_METADATA";
/* 63 */   public static final byte[] ID_AZ_METADATA_BYTES = "AZ_METADATA".getBytes();
/*    */   
/*    */ 
/*    */   public static final int SUBID_ID_AZ_METADATA = 8;
/*    */   
/*    */   public static final String ID_AZ_SESSION_SYN = "AZ_SESSION_SYN";
/*    */   
/* 70 */   public static final byte[] ID_AZ_SESSION_SYN_BYTES = "AZ_SESSION_SYN".getBytes();
/*    */   
/*    */   public static final String ID_AZ_SESSION_ACK = "AZ_SESSION_ACK";
/* 73 */   public static final byte[] ID_AZ_SESSION_ACK_BYTES = "AZ_SESSION_ACK".getBytes();
/*    */   
/*    */   public static final String ID_AZ_SESSION_END = "AZ_SESSION_END";
/* 76 */   public static final byte[] ID_AZ_SESSION_END_BYTES = "AZ_SESSION_END".getBytes();
/*    */   
/*    */   public static final String ID_AZ_SESSION_BITFIELD = "AZ_SESSION_BITFIELD";
/* 79 */   public static final byte[] ID_AZ_SESSION_BITFIELD_BYTES = "AZ_SESSION_BITFIELD".getBytes();
/*    */   
/*    */   public static final String ID_AZ_SESSION_CANCEL = "AZ_SESSION_CANCEL";
/* 82 */   public static final byte[] ID_AZ_SESSION_CANCEL_BYTES = "AZ_SESSION_CANCEL".getBytes();
/*    */   
/*    */   public static final String ID_AZ_SESSION_HAVE = "AZ_SESSION_HAVE";
/* 85 */   public static final byte[] ID_AZ_SESSION_HAVE_BYTES = "AZ_SESSION_HAVE".getBytes();
/*    */   
/*    */   public static final String ID_AZ_SESSION_PIECE = "AZ_SESSION_PIECE";
/* 88 */   public static final byte[] ID_AZ_SESSION_PIECE_BYTES = "AZ_SESSION_PIECE".getBytes();
/*    */   
/*    */   public static final String ID_AZ_SESSION_REQUEST = "AZ_SESSION_REQUEST";
/* 91 */   public static final byte[] ID_AZ_SESSION_REQUEST_BYTES = "AZ_SESSION_REQUEST".getBytes();
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/azureus/AZMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */