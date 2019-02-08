/*     */ package com.aelitis.azureus.core.util;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
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
/*     */ public class PlatformTorrentUtils
/*     */ {
/*     */   private static final String TOR_AZ_PROP_MAP = "Content";
/*     */   private static final String TOR_AZ_PROP_CVERSION = "_Version_";
/*     */   private static final String TOR_AZ_PROP_TITLE = "Title";
/*     */   private static final String TOR_AZ_PROP_DESCRIPTION = "Description";
/*     */   private static final String TOR_AZ_PROP_PRIMARY_FILE = "Primary File Index";
/*     */   private static final String TOR_AZ_PROP_THUMBNAIL = "Thumbnail";
/*     */   private static final String TOR_AZ_PROP_THUMBNAIL_TYPE = "Thumbnail.type";
/*     */   private static final String TOR_AZ_PROP_THUMBNAIL_URL = "Thumbnail.url";
/*     */   
/*     */   public static Map getContentMap(TOTorrent torrent)
/*     */   {
/*  54 */     if (torrent == null) {
/*  55 */       return Collections.EMPTY_MAP;
/*     */     }
/*     */     
/*  58 */     Map mapAZProps = torrent.getAdditionalMapProperty("azureus_properties");
/*     */     
/*  60 */     if (mapAZProps == null) {
/*  61 */       mapAZProps = new HashMap();
/*  62 */       torrent.setAdditionalMapProperty("azureus_properties", mapAZProps);
/*     */     }
/*     */     
/*  65 */     Object objExistingContentMap = mapAZProps.get("Content");
/*     */     Map mapContent;
/*     */     Map mapContent;
/*  68 */     if ((objExistingContentMap instanceof Map)) {
/*  69 */       mapContent = (Map)objExistingContentMap;
/*     */     } else {
/*  71 */       mapContent = new HashMap();
/*  72 */       mapAZProps.put("Content", mapContent);
/*     */     }
/*     */     
/*  75 */     return mapContent;
/*     */   }
/*     */   
/*     */   static Map getTempContentMap(TOTorrent torrent) {
/*  79 */     if (torrent == null) {
/*  80 */       return new HashMap();
/*     */     }
/*     */     
/*  83 */     Map mapAZProps = torrent.getAdditionalMapProperty("attributes");
/*     */     
/*  85 */     if (mapAZProps == null) {
/*  86 */       mapAZProps = new HashMap();
/*  87 */       torrent.setAdditionalMapProperty("attributes", mapAZProps);
/*     */     }
/*     */     
/*  90 */     Object objExistingContentMap = mapAZProps.get("Content");
/*     */     Map mapContent;
/*     */     Map mapContent;
/*  93 */     if ((objExistingContentMap instanceof Map)) {
/*  94 */       mapContent = (Map)objExistingContentMap;
/*     */     } else {
/*  96 */       mapContent = new HashMap();
/*  97 */       mapAZProps.put("Content", mapContent);
/*     */     }
/*     */     
/* 100 */     return mapContent;
/*     */   }
/*     */   
/*     */   public static String getContentMapString(TOTorrent torrent, String key) {
/* 104 */     if (torrent == null) {
/* 105 */       return null;
/*     */     }
/*     */     
/* 108 */     Map mapContent = getContentMap(torrent);
/* 109 */     Object obj = mapContent.get(key);
/*     */     
/* 111 */     if ((obj instanceof String))
/* 112 */       return (String)obj;
/* 113 */     if ((obj instanceof byte[])) {
/*     */       try {
/* 115 */         return new String((byte[])obj, "UTF8");
/*     */       }
/*     */       catch (UnsupportedEncodingException e) {
/* 118 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/* 122 */     return null;
/*     */   }
/*     */   
/*     */   private static void setContentMapString(TOTorrent torrent, String key, String value)
/*     */   {
/* 127 */     if (torrent == null) {
/* 128 */       return;
/*     */     }
/*     */     
/* 131 */     Map mapContent = getContentMap(torrent);
/* 132 */     mapContent.put(key, value);
/* 133 */     incVersion(mapContent);
/*     */   }
/*     */   
/*     */   private static long getContentMapLong(TOTorrent torrent, String key, long def) {
/* 137 */     if (torrent == null) {
/* 138 */       return def;
/*     */     }
/*     */     
/* 141 */     Map mapContent = getContentMap(torrent);
/* 142 */     Object obj = mapContent.get(key);
/*     */     try
/*     */     {
/* 145 */       if ((obj instanceof Long))
/* 146 */         return ((Long)obj).longValue();
/* 147 */       if ((obj instanceof Integer))
/* 148 */         return ((Integer)obj).longValue();
/* 149 */       if ((obj instanceof String))
/* 150 */         return Long.parseLong((String)obj);
/* 151 */       if ((obj instanceof byte[])) {
/* 152 */         return Long.parseLong(new String((byte[])obj));
/*     */       }
/*     */     }
/*     */     catch (Exception e) {}
/*     */     
/* 157 */     return def;
/*     */   }
/*     */   
/*     */   public static Map getContentMapMap(TOTorrent torrent, String key) {
/* 161 */     if (torrent == null) {
/* 162 */       return null;
/*     */     }
/*     */     
/* 165 */     Map mapContent = getContentMap(torrent);
/* 166 */     Object obj = mapContent.get(key);
/*     */     
/* 168 */     if ((obj instanceof Map)) {
/* 169 */       return (Map)obj;
/*     */     }
/*     */     
/* 172 */     return null;
/*     */   }
/*     */   
/*     */   private static void setContentMapLong(TOTorrent torrent, String key, long value)
/*     */   {
/* 177 */     if (torrent == null) {
/* 178 */       return;
/*     */     }
/*     */     
/* 181 */     Map mapContent = getContentMap(torrent);
/* 182 */     mapContent.put(key, new Long(value));
/* 183 */     incVersion(mapContent);
/*     */   }
/*     */   
/*     */   public static void setContentMapMap(TOTorrent torrent, String key, Map value)
/*     */   {
/* 188 */     if (torrent == null) {
/* 189 */       return;
/*     */     }
/*     */     
/* 192 */     Map mapContent = getContentMap(torrent);
/* 193 */     mapContent.put(key, value);
/* 194 */     incVersion(mapContent);
/*     */   }
/*     */   
/*     */   private static void putOrRemove(Map map, String key, Object obj) {
/* 198 */     if (obj == null) {
/* 199 */       map.remove(key);
/*     */     } else {
/* 201 */       map.put(key, obj);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void writeTorrentIfExists(TOTorrent torrent) {
/* 206 */     if (!AzureusCoreFactory.isCoreRunning()) {
/* 207 */       return;
/*     */     }
/* 209 */     AzureusCore core = AzureusCoreFactory.getSingleton();
/* 210 */     if ((core == null) || (!core.isStarted())) {
/* 211 */       return;
/*     */     }
/*     */     
/* 214 */     GlobalManager gm = core.getGlobalManager();
/* 215 */     if ((gm == null) || (gm.getDownloadManager(torrent) == null)) {
/* 216 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 220 */       TorrentUtils.writeToFile(torrent);
/*     */     } catch (TOTorrentException e) {
/* 222 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static void incVersion(Map mapContent)
/*     */   {
/* 230 */     Long v = (Long)mapContent.get("_Version_");
/* 231 */     mapContent.put("_Version_", Long.valueOf(v == null ? 0L : v.longValue() + 1L));
/*     */   }
/*     */   
/*     */   public static int getContentVersion(TOTorrent torrent) {
/* 235 */     Map mapContent = getContentMap(torrent);
/* 236 */     Long v = (Long)mapContent.get("_Version_");
/* 237 */     return v == null ? 0 : v.intValue();
/*     */   }
/*     */   
/*     */   public static String getContentTitle(TOTorrent torrent) {
/* 241 */     return getContentMapString(torrent, "Title");
/*     */   }
/*     */   
/* 244 */   public static void setContentTitle(TOTorrent torrent, String title) { setContentMapString(torrent, "Title", title); }
/*     */   
/*     */   public static byte[] getContentThumbnail(TOTorrent torrent)
/*     */   {
/* 248 */     Map mapContent = getContentMap(torrent);
/* 249 */     Object obj = mapContent.get("Thumbnail");
/*     */     
/* 251 */     if ((obj instanceof byte[])) {
/* 252 */       return (byte[])obj;
/*     */     }
/*     */     
/* 255 */     return null;
/*     */   }
/*     */   
/*     */   public static String getContentDescription(TOTorrent torrent) {
/* 259 */     return getContentMapString(torrent, "Description");
/*     */   }
/*     */   
/*     */   public static void setContentDescription(TOTorrent torrent, String desc) {
/* 263 */     setContentMapString(torrent, "Description", desc);
/* 264 */     writeTorrentIfExists(torrent);
/*     */   }
/*     */   
/*     */   public static String getContentThumbnailUrl(TOTorrent torrent) {
/* 268 */     return getContentMapString(torrent, "Thumbnail.url");
/*     */   }
/*     */   
/*     */   public static void setContentThumbnailUrl(TOTorrent torrent, String url) {
/* 272 */     setContentMapString(torrent, "Thumbnail.url", url);
/*     */   }
/*     */   
/*     */ 
/*     */   public static void setContentThumbnail(TOTorrent torrent, byte[] thumbnail)
/*     */   {
/* 278 */     Map mapContent = getContentMap(torrent);
/* 279 */     putOrRemove(mapContent, "Thumbnail", thumbnail);
/* 280 */     incVersion(mapContent);
/* 281 */     writeTorrentIfExists(torrent);
/*     */   }
/*     */   
/*     */   public static void setContentThumbnail(TOTorrent torrent, byte[] thumbnail, String type) {
/* 285 */     Map mapContent = getContentMap(torrent);
/* 286 */     putOrRemove(mapContent, "Thumbnail", thumbnail);
/* 287 */     incVersion(mapContent);
/* 288 */     setContentMapString(torrent, "Thumbnail.type", type);
/* 289 */     writeTorrentIfExists(torrent);
/*     */   }
/*     */   
/*     */   public static String getContentThumbnailType(TOTorrent torrent) {
/* 293 */     return getContentMapString(torrent, "Thumbnail.type");
/*     */   }
/*     */   
/*     */   public static int getContentPrimaryFileIndex(TOTorrent torrent) {
/* 297 */     return (int)getContentMapLong(torrent, "Primary File Index", -1L);
/*     */   }
/*     */   
/*     */   public static void setContentPrimaryFileIndex(TOTorrent torrent, int index) {
/* 301 */     setContentMapLong(torrent, "Primary File Index", index);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/PlatformTorrentUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */