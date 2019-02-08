/*     */ package com.aelitis.azureus.core.metasearch;
/*     */ 
/*     */ import com.aelitis.azureus.core.subs.Subscription;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract interface Engine
/*     */ {
/*     */   public static final int AZ_VERSION = 5;
/*  47 */   public static final Object VUZE_FILE_COMPONENT_ENGINE_KEY = new Object();
/*     */   
/*     */   public static final int FIELD_NAME = 1;
/*     */   
/*     */   public static final int FIELD_DATE = 2;
/*     */   
/*     */   public static final int FIELD_SIZE = 3;
/*     */   
/*     */   public static final int FIELD_PEERS = 4;
/*     */   
/*     */   public static final int FIELD_SEEDS = 5;
/*     */   
/*     */   public static final int FIELD_CATEGORY = 6;
/*     */   
/*     */   public static final int FIELD_COMMENTS = 7;
/*     */   public static final int FIELD_CONTENT_TYPE = 8;
/*     */   public static final int FIELD_DISCARD = 9;
/*     */   public static final int FIELD_VOTES = 10;
/*     */   public static final int FIELD_SUPERSEEDS = 11;
/*     */   public static final int FIELD_PRIVATE = 12;
/*     */   public static final int FIELD_DRMKEY = 13;
/*     */   public static final int FIELD_VOTES_DOWN = 14;
/*     */   public static final int FIELD_TORRENTLINK = 102;
/*     */   public static final int FIELD_CDPLINK = 103;
/*     */   public static final int FIELD_PLAYLINK = 104;
/*     */   public static final int FIELD_DOWNLOADBTNLINK = 105;
/*     */   public static final int FIELD_HASH = 200;
/*     */   public static final int FIELD_RANK = 201;
/*  75 */   public static final int[] FIELD_IDS = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 102, 103, 104, 105, 10, 11, 12, 13, 14, 200, 201 };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  82 */   public static final String[] FIELD_NAMES = { "TITLE", "DATE", "SIZE", "PEERS", "SEEDS", "CAT", "COMMENTS", "CONTENT_TYPE", "DISCARD", "TORRENT", "CDP", "PLAY", "DLBTN", "VOTES", "XSEEDS", "PRIVATE", "DRMKEY", "VOTESDOWN", "HASH", "RANK" };
/*     */   
/*     */   public static final int ENGINE_TYPE_REGEX = 1;
/*     */   
/*     */   public static final int ENGINE_TYPE_JSON = 2;
/*     */   
/*     */   public static final int ENGINE_TYPE_PLUGIN = 3;
/*     */   
/*     */   public static final int ENGINE_TYPE_RSS = 4;
/*     */   
/*     */   public static final int ENGINE_SOURCE_UNKNOWN = 0;
/*     */   
/*     */   public static final int ENGINE_SOURCE_VUZE = 1;
/*     */   
/*     */   public static final int ENGINE_SOURCE_LOCAL = 2;
/*     */   
/*     */   public static final int ENGINE_SOURCE_RSS = 3;
/*     */   
/*     */   public static final int SEL_STATE_DESELECTED = 0;
/*     */   
/*     */   public static final int SEL_STATE_AUTO_SELECTED = 1;
/*     */   
/*     */   public static final int SEL_STATE_MANUAL_SELECTED = 2;
/*     */   
/*     */   public static final int SEL_STATE_FORCE_DESELECTED = 3;
/*     */   
/*     */   public static final int AUTO_DL_SUPPORTED_UNKNOWN = 0;
/*     */   
/*     */   public static final int AUTO_DL_SUPPORTED_YES = 1;
/*     */   public static final int AUTO_DL_SUPPORTED_NO = 2;
/* 112 */   public static final String[] ENGINE_SOURCE_STRS = { "unknown", "vuze", "local", "rss", "unused" };
/* 113 */   public static final String[] SEL_STATE_STRINGS = { "no", "auto", "manual", "force_no" };
/* 114 */   public static final String[] ENGINE_TYPE_STRS = { "unknown", "regexp", "json", "plugin" };
/*     */   public static final String SC_SOURCE = "azsrc";
/*     */   public static final String SC_AZID = "azid";
/*     */   public static final String SC_FORCE_FULL = "force_full";
/*     */   public static final String SC_BATCH_PERIOD = "batch_millis";
/*     */   public static final String SC_REMOVE_DUP_HASH = "remove_dup_hash";
/*     */   public static final String CT_VIDEO = "video";
/*     */   public static final String CT_AUDIO = "audio";
/*     */   public static final String CT_GAME = "game";
/*     */   
/*     */   public abstract int getType();
/*     */   
/*     */   public abstract Result[] search(SearchParameter[] paramArrayOfSearchParameter, Map paramMap, int paramInt1, int paramInt2, String paramString, ResultListener paramResultListener)
/*     */     throws SearchException;
/*     */   
/*     */   public abstract String getName();
/*     */   
/*     */   public abstract String getNameEx();
/*     */   
/*     */   public abstract long getId();
/*     */   
/*     */   public abstract String getUID();
/*     */   
/*     */   public abstract int getVersion();
/*     */   
/*     */   public abstract long getLastUpdated();
/*     */   
/*     */   public abstract String getIcon();
/*     */   
/*     */   public abstract String getDownloadLinkCSS();
/*     */   
/*     */   public abstract boolean isActive();
/*     */   
/*     */   public abstract boolean isMine();
/*     */   
/*     */   public abstract boolean isPublic();
/*     */   
/*     */   public abstract void setMine(boolean paramBoolean);
/*     */   
/*     */   public abstract int getSelectionState();
/*     */   
/*     */   public abstract void setSelectionState(int paramInt);
/*     */   
/*     */   public abstract void recordSelectionState();
/*     */   
/*     */   public abstract void checkSelectionStateRecorded();
/*     */   
/*     */   public abstract int getSource();
/*     */   
/*     */   public abstract void setSource(int paramInt);
/*     */   
/*     */   public abstract String getReferer();
/*     */   
/*     */   public abstract float getRankBias();
/*     */   
/*     */   public abstract void setRankBias(float paramFloat);
/*     */   
/*     */   public abstract void setPreferredDelta(float paramFloat);
/*     */   
/*     */   public abstract float getPreferredWeighting();
/*     */   
/*     */   public abstract float applyRankBias(float paramFloat);
/*     */   
/*     */   public abstract boolean supportsField(int paramInt);
/*     */   
/*     */   public abstract boolean supportsContext(String paramString);
/*     */   
/*     */   public abstract boolean isShareable();
/*     */   
/*     */   public abstract boolean isAnonymous();
/*     */   
/*     */   public abstract boolean isAuthenticated();
/*     */   
/*     */   public abstract int getAutoDownloadSupported();
/*     */   
/*     */   public abstract int getAZVersion();
/*     */   
/*     */   public abstract void addPotentialAssociation(String paramString);
/*     */   
/*     */   public abstract Subscription getSubscription();
/*     */   
/*     */   public abstract Map<String, Object> exportToBencodedMap()
/*     */     throws IOException;
/*     */   
/*     */   public abstract Map<String, Object> exportToBencodedMap(boolean paramBoolean)
/*     */     throws IOException;
/*     */   
/*     */   public abstract String exportToJSONString()
/*     */     throws IOException;
/*     */   
/*     */   public abstract void exportToVuzeFile(File paramFile)
/*     */     throws IOException;
/*     */   
/*     */   public abstract VuzeFile exportToVuzeFile()
/*     */     throws IOException;
/*     */   
/*     */   public abstract boolean sameLogicAs(Engine paramEngine);
/*     */   
/*     */   public abstract void reset();
/*     */   
/*     */   public abstract void delete();
/*     */   
/*     */   public abstract String getString();
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/Engine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */