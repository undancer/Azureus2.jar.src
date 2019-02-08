/*    */ package com.aelitis.azureus.core.peermanager.messaging.bittorrent.ltep;
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
/*    */ public abstract interface LTMessage
/*    */   extends Message
/*    */ {
/*    */   public static final String LT_FEATURE_ID = "LT1";
/*    */   public static final String ID_LT_HANDSHAKE = "lt_handshake";
/* 31 */   public static final byte[] ID_LT_HANDSHAKE_BYTES = "lt_handshake".getBytes();
/*    */   
/*    */   public static final int SUBID_LT_HANDSHAKE = 0;
/*    */   public static final String ID_UT_PEX = "ut_pex";
/* 35 */   public static final byte[] ID_UT_PEX_BYTES = "ut_pex".getBytes();
/*    */   
/*    */   public static final int SUBID_UT_PEX = 1;
/*    */   
/*    */   public static final String ID_DISABLED_EXT = "disabled_extension";
/*    */   
/* 41 */   public static final byte[] ID_DISABLED_EXT_BYTES = "disabled_extension".getBytes();
/*    */   
/*    */   public static final int SUBID_DISABLED_EXT = 2;
/*    */   public static final String ID_UT_METADATA = "ut_metadata";
/* 45 */   public static final byte[] ID_UT_METADATA_BYTES = "ut_metadata".getBytes();
/*    */   
/*    */   public static final int SUBID_UT_METADATA = 3;
/*    */   public static final String ID_UT_UPLOAD_ONLY = "upload_only";
/* 49 */   public static final byte[] ID_UT_UPLOAD_ONLY_BYTES = "upload_only".getBytes();
/*    */   public static final int SUBID_UT_UPLOAD_ONLY = 4;
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/ltep/LTMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */