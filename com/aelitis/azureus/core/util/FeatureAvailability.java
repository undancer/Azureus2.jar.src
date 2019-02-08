/*     */ package com.aelitis.azureus.core.util;
/*     */ 
/*     */ import com.aelitis.azureus.core.versioncheck.VersionCheckClient;
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
/*     */ public class FeatureAvailability
/*     */ {
/*     */   private static final long FT_DISABLE_REQUEST_LIMITING = 1L;
/*     */   private static final long FT_DISABLE_PEER_GENERAL_RECONNECT = 2L;
/*     */   private static final long FT_DISABLE_PEER_UDP_RECONNECT = 4L;
/*     */   private static final long FT_AUTO_SPEED_DEFAULT_CLASSIC = 8L;
/*     */   private static final long FT_DISABLE_RCM = 16L;
/*     */   private static final long FT_DISABLE_DHT_REP_V2 = 32L;
/*     */   private static final long FT_DISABLE_MAGNET_SL = 64L;
/*     */   private static final long FT_ENABLE_ALL_FE_CLIENTS = 128L;
/*     */   private static final long FT_ENABLE_INTERNAL_FEATURES = 256L;
/*     */   private static final long FT_TRIGGER_SPEED_TEST_V1 = 512L;
/*     */   private static final long FT_DISABLE_GAMES = 1024L;
/*     */   private static final long FT_DISABLE_MAGNET_MD = 2048L;
/*  43 */   private static final VersionCheckClient vcc = ;
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
/*     */   public static boolean areInternalFeaturesEnabled()
/*     */   {
/*  56 */     boolean result = (vcc.getFeatureFlags() & 0x100) != 0L;
/*     */     
/*  58 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean isRequestLimitingEnabled()
/*     */   {
/*  64 */     boolean result = (vcc.getFeatureFlags() & 1L) == 0L;
/*     */     
/*  66 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean isGeneralPeerReconnectEnabled()
/*     */   {
/*  72 */     boolean result = (vcc.getFeatureFlags() & 0x2) == 0L;
/*     */     
/*  74 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean isUDPPeerReconnectEnabled()
/*     */   {
/*  80 */     boolean result = (vcc.getFeatureFlags() & 0x4) == 0L;
/*     */     
/*  82 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean isAutoSpeedDefaultClassic()
/*     */   {
/*  88 */     boolean result = (vcc.getFeatureFlags() & 0x8) != 0L;
/*     */     
/*  90 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean isRCMEnabled()
/*     */   {
/*  96 */     boolean result = (vcc.getFeatureFlags() & 0x10) == 0L;
/*     */     
/*  98 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean isDHTRepV2Enabled()
/*     */   {
/* 104 */     boolean result = (vcc.getFeatureFlags() & 0x20) == 0L;
/*     */     
/* 106 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean isMagnetSLEnabled()
/*     */   {
/* 112 */     boolean result = (vcc.getFeatureFlags() & 0x40) == 0L;
/*     */     
/* 114 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean isMagnetMDEnabled()
/*     */   {
/* 120 */     boolean result = (vcc.getFeatureFlags() & 0x800) == 0L;
/*     */     
/* 122 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean allowAllFEClients()
/*     */   {
/* 128 */     boolean result = (vcc.getFeatureFlags() & 0x80) != 0L;
/*     */     
/* 130 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean triggerSpeedTestV1()
/*     */   {
/* 136 */     boolean result = (vcc.getFeatureFlags() & 0x200) != 0L;
/*     */     
/* 138 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean isGamesEnabled()
/*     */   {
/* 144 */     boolean result = (vcc.getFeatureFlags() & 0x400) == 0L;
/*     */     
/* 146 */     return result;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/FeatureAvailability.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */