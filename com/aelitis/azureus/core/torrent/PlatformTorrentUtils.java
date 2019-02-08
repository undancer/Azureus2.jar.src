/*     */ package com.aelitis.azureus.core.torrent;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnosticsLogger;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.pluginsimpl.local.torrent.TorrentImpl;
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
/*     */   private static final long MIN_SPEED_DEFAULT = 102400L;
/*     */   public static final String AELITIS_HOST_CORE = ".aelitis.com";
/*     */   public static final String VUZE_HOST_CORE = ".vuze.com";
/*  52 */   public static final boolean DEBUG_CACHING = System.getProperty("az3.debug.caching", "0").equals("1");
/*     */   
/*     */ 
/*     */   private static final String TOR_AZ_PROP_MAP = "Content";
/*     */   
/*     */   private static final String TOR_AZ_PROP_HASH = "Content Hash";
/*     */   
/*     */   private static final String TOR_AZ_PROP_TITLE = "Title";
/*     */   
/*     */   private static final String TOR_AZ_PROP_DESCRIPTION = "Description";
/*     */   
/*     */   private static final String TOR_AZ_PROP_CONTENT_TYPE = "Content Type";
/*     */   
/*     */   private static final String TOR_AZ_PROP_AUTHOR = "Author";
/*     */   
/*     */   private static final String TOR_AZ_PROP_PUBLISHER = "Publisher";
/*     */   
/*     */   private static final String TOR_AZ_PROP_URL = "URL";
/*     */   
/*     */   private static final String TOR_AZ_PROP_THUMBNAIL = "Thumbnail";
/*     */   
/*     */   private static final String TOR_AZ_PROP_THUMBNAIL_URL = "Thumbnail.url";
/*     */   
/*     */   private static final String TOR_AZ_PROP_PROGRESSIVE = "Progressive";
/*     */   
/*     */   private static final String TOR_AZ_PROP_SPEED = "Speed Bps";
/*     */   
/*     */   private static final String TOR_AZ_PROP_MIN_SPEED = "Min Speed Bps";
/*     */   
/*     */   private static final String TOR_AZ_PROP_QOS_CLASS = "QOS Class";
/*     */   
/*     */   private static final String TOR_AZ_PROP_CONTENT_NETWORK = "Content Network";
/*     */   
/*     */   private static final String TOR_AZ_PROP_EXPIRESON = "Expires On";
/*     */   
/*     */   private static final String TOR_AZ_PROP_PRIMARY_FILE = "Primary File Index";
/*     */   
/*  89 */   private static final ArrayList<HasBeenOpenedListener> hasBeenOpenedListeners = new ArrayList(1);
/*     */   
/*     */   private static final String TOR_AZ_PROP_VIDEO_WIDTH = "Video Width";
/*     */   
/*     */   private static final String TOR_AZ_PROP_VIDEO_HEIGHT = "Video Height";
/*     */   
/*     */   private static final String TOR_AZ_PROP_VIDEO_RUNNINGTIME = "Running Time";
/*     */   
/*     */   private static final String TOR_AZ_PROP_DURATION_MILLIS = "Duration";
/*     */   
/*     */   private static final String TOR_AZ_PROP_OPENED = "Opened";
/*     */   
/* 101 */   private static ArrayList<String> listPlatformHosts = new ArrayList();
/*     */   
/*     */   static {
/* 104 */     for (int i = 0; i < Constants.AZUREUS_DOMAINS.length; i++) {
/* 105 */       listPlatformHosts.add(Constants.AZUREUS_DOMAINS[i].toLowerCase());
/*     */     }
/*     */   }
/*     */   
/* 109 */   private static final Map mapPlatformTrackerTorrents = new WeakHashMap();
/*     */   
/* 111 */   private static boolean embeddedPlayerAvail = false;
/*     */   
/*     */   public static Map getContentMap(TOTorrent torrent) {
/* 114 */     if (torrent == null) {
/* 115 */       return Collections.EMPTY_MAP;
/*     */     }
/*     */     
/* 118 */     Map mapAZProps = torrent.getAdditionalMapProperty("azureus_properties");
/*     */     
/* 120 */     if (mapAZProps == null) {
/* 121 */       mapAZProps = new HashMap();
/* 122 */       torrent.setAdditionalMapProperty("azureus_properties", mapAZProps);
/*     */     }
/*     */     
/* 125 */     Object objExistingContentMap = mapAZProps.get("Content");
/*     */     Map mapContent;
/*     */     Map mapContent;
/* 128 */     if ((objExistingContentMap instanceof Map)) {
/* 129 */       mapContent = (Map)objExistingContentMap;
/*     */     } else {
/* 131 */       mapContent = new HashMap();
/* 132 */       mapAZProps.put("Content", mapContent);
/*     */     }
/*     */     
/* 135 */     return mapContent;
/*     */   }
/*     */   
/*     */   static Map getTempContentMap(TOTorrent torrent) {
/* 139 */     if (torrent == null) {
/* 140 */       return new HashMap();
/*     */     }
/*     */     
/* 143 */     Map mapAZProps = torrent.getAdditionalMapProperty("attributes");
/*     */     
/* 145 */     if (mapAZProps == null) {
/* 146 */       mapAZProps = new HashMap();
/* 147 */       torrent.setAdditionalMapProperty("attributes", mapAZProps);
/*     */     }
/*     */     
/* 150 */     Object objExistingContentMap = mapAZProps.get("Content");
/*     */     Map mapContent;
/*     */     Map mapContent;
/* 153 */     if ((objExistingContentMap instanceof Map)) {
/* 154 */       mapContent = (Map)objExistingContentMap;
/*     */     } else {
/* 156 */       mapContent = new HashMap();
/* 157 */       mapAZProps.put("Content", mapContent);
/*     */     }
/*     */     
/* 160 */     return mapContent;
/*     */   }
/*     */   
/*     */   public static String getContentMapString(TOTorrent torrent, String key) {
/* 164 */     if (torrent == null) {
/* 165 */       return null;
/*     */     }
/*     */     
/* 168 */     Map mapContent = getContentMap(torrent);
/* 169 */     Object obj = mapContent.get(key);
/*     */     
/* 171 */     if ((obj instanceof String))
/* 172 */       return (String)obj;
/* 173 */     if ((obj instanceof byte[])) {
/*     */       try {
/* 175 */         return new String((byte[])obj, "UTF8");
/*     */       }
/*     */       catch (UnsupportedEncodingException e) {
/* 178 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/* 182 */     return null;
/*     */   }
/*     */   
/*     */   private static void setContentMapString(TOTorrent torrent, String key, String value)
/*     */   {
/* 187 */     if (torrent == null) {
/* 188 */       return;
/*     */     }
/*     */     
/* 191 */     Map mapContent = getContentMap(torrent);
/* 192 */     mapContent.put(key, value);
/*     */   }
/*     */   
/*     */   private static long getContentMapLong(TOTorrent torrent, String key, long def) {
/* 196 */     if (torrent == null) {
/* 197 */       return def;
/*     */     }
/*     */     
/* 200 */     Map mapContent = getContentMap(torrent);
/* 201 */     Object obj = mapContent.get(key);
/*     */     try
/*     */     {
/* 204 */       if ((obj instanceof Long))
/* 205 */         return ((Long)obj).longValue();
/* 206 */       if ((obj instanceof Integer))
/* 207 */         return ((Integer)obj).longValue();
/* 208 */       if ((obj instanceof String))
/* 209 */         return Long.parseLong((String)obj);
/* 210 */       if ((obj instanceof byte[])) {
/* 211 */         return Long.parseLong(new String((byte[])obj));
/*     */       }
/*     */     }
/*     */     catch (Exception e) {}
/*     */     
/* 216 */     return def;
/*     */   }
/*     */   
/*     */   public static Map getContentMapMap(TOTorrent torrent, String key) {
/* 220 */     if (torrent == null) {
/* 221 */       return null;
/*     */     }
/*     */     
/* 224 */     Map mapContent = getContentMap(torrent);
/* 225 */     Object obj = mapContent.get(key);
/*     */     
/* 227 */     if ((obj instanceof Map)) {
/* 228 */       return (Map)obj;
/*     */     }
/*     */     
/* 231 */     return null;
/*     */   }
/*     */   
/*     */   private static void setContentMapLong(TOTorrent torrent, String key, long value)
/*     */   {
/* 236 */     if (torrent == null) {
/* 237 */       return;
/*     */     }
/*     */     
/* 240 */     Map mapContent = getContentMap(torrent);
/* 241 */     mapContent.put(key, new Long(value));
/*     */   }
/*     */   
/*     */   public static void setContentMapMap(TOTorrent torrent, String key, Map value)
/*     */   {
/* 246 */     if (torrent == null) {
/* 247 */       return;
/*     */     }
/*     */     
/* 250 */     Map mapContent = getContentMap(torrent);
/* 251 */     mapContent.put(key, value);
/*     */   }
/*     */   
/*     */   public static String getContentHash(TOTorrent torrent) {
/* 255 */     return getContentMapString(torrent, "Content Hash");
/*     */   }
/*     */   
/*     */   public static String getContentTitle(TOTorrent torrent) {
/* 259 */     return getContentMapString(torrent, "Title");
/*     */   }
/*     */   
/*     */   public static void setContentTitle(TOTorrent torrent, String title) {
/* 263 */     setContentMapString(torrent, "Title", title);
/*     */   }
/*     */   
/*     */   public static String getContentDescription(TOTorrent torrent) {
/* 267 */     return getContentMapString(torrent, "Description");
/*     */   }
/*     */   
/*     */   public static void setContentDescription(TOTorrent torrent, String desc) {
/* 271 */     setContentMapString(torrent, "Description", desc);
/*     */   }
/*     */   
/*     */   public static String getContentType(TOTorrent torrent) {
/* 275 */     return getContentMapString(torrent, "Content Type");
/*     */   }
/*     */   
/*     */   public static void setContentType(TOTorrent torrent, String title) {
/* 279 */     setContentMapString(torrent, "Content Type", title);
/*     */   }
/*     */   
/*     */   public static String getContentAuthor(TOTorrent torrent) {
/* 283 */     return getContentMapString(torrent, "Author");
/*     */   }
/*     */   
/*     */   public static String getContentPublisher(TOTorrent torrent) {
/* 287 */     return getContentMapString(torrent, "Publisher");
/*     */   }
/*     */   
/*     */   public static String getContentURL(TOTorrent torrent) {
/* 291 */     return getContentMapString(torrent, "URL");
/*     */   }
/*     */   
/*     */   public static long getQOSClass(TOTorrent torrent) {
/* 295 */     return getContentMapLong(torrent, "QOS Class", 0L);
/*     */   }
/*     */   
/*     */   public static void setQOSClass(TOTorrent torrent, long cla) {
/* 299 */     setContentMapLong(torrent, "QOS Class", cla);
/*     */   }
/*     */   
/*     */   public static long getContentNetworkID(TOTorrent torrent) {
/* 303 */     return getContentNetworkID(torrent, -1L);
/*     */   }
/*     */   
/*     */   public static long getContentNetworkID(TOTorrent torrent, long def) {
/* 307 */     long id = getContentMapLong(torrent, "Content Network", -1L);
/*     */     
/* 309 */     if (id == -1L) {
/* 310 */       return isContent(torrent, false) ? 1L : def;
/*     */     }
/*     */     
/* 313 */     return id;
/*     */   }
/*     */   
/*     */   public static void setContentNetworkID(TOTorrent torrent, long cnet) {
/* 317 */     setContentMapLong(torrent, "Content Network", cnet);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean isFeaturedContent(TOTorrent torrent)
/*     */   {
/* 324 */     String content_type = getContentType(torrent);
/*     */     
/* 326 */     return (content_type != null) && (content_type.equalsIgnoreCase("featured"));
/*     */   }
/*     */   
/*     */   private static void putOrRemove(Map map, String key, Object obj) {
/* 330 */     if (obj == null) {
/* 331 */       map.remove(key);
/*     */     } else {
/* 333 */       map.put(key, obj);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void writeTorrentIfExists(TOTorrent torrent) {
/* 338 */     if (!AzureusCoreFactory.isCoreRunning()) {
/* 339 */       return;
/*     */     }
/* 341 */     AzureusCore core = AzureusCoreFactory.getSingleton();
/* 342 */     if ((core == null) || (!core.isStarted())) {
/* 343 */       return;
/*     */     }
/*     */     
/* 346 */     GlobalManager gm = core.getGlobalManager();
/* 347 */     if ((gm == null) || (gm.getDownloadManager(torrent) == null)) {
/* 348 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 352 */       TorrentUtils.writeToFile(torrent);
/*     */     } catch (TOTorrentException e) {
/* 354 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public static byte[] getContentThumbnail(TOTorrent torrent) {
/* 359 */     Map mapContent = getContentMap(torrent);
/* 360 */     Object obj = mapContent.get("Thumbnail");
/*     */     
/* 362 */     if ((obj instanceof byte[])) {
/* 363 */       return (byte[])obj;
/*     */     }
/*     */     
/* 366 */     return null;
/*     */   }
/*     */   
/*     */   public static String getContentThumbnailUrl(TOTorrent torrent) {
/* 370 */     return getContentMapString(torrent, "Thumbnail.url");
/*     */   }
/*     */   
/*     */   public static void setContentThumbnailUrl(TOTorrent torrent, String url) {
/* 374 */     setContentMapString(torrent, "Thumbnail.url", url);
/*     */   }
/*     */   
/*     */   public static void setContentThumbnail(TOTorrent torrent, byte[] thumbnail) {
/* 378 */     Map mapContent = getContentMap(torrent);
/* 379 */     putOrRemove(mapContent, "Thumbnail", thumbnail);
/*     */     
/* 381 */     writeTorrentIfExists(torrent);
/*     */   }
/*     */   
/*     */   public static boolean isContent(TOTorrent torrent, boolean requirePlatformTracker)
/*     */   {
/* 386 */     if (torrent == null) {
/* 387 */       return false;
/*     */     }
/* 389 */     boolean bContent = getContentHash(torrent) != null;
/* 390 */     if ((!bContent) || ((bContent) && (!requirePlatformTracker))) {
/* 391 */       return bContent;
/*     */     }
/*     */     
/* 394 */     return isPlatformTracker(torrent);
/*     */   }
/*     */   
/*     */   public static boolean isContent(Torrent torrent, boolean requirePlatformTracker)
/*     */   {
/* 399 */     if ((torrent instanceof TorrentImpl)) {
/* 400 */       return isContent(((TorrentImpl)torrent).getTorrent(), requirePlatformTracker);
/*     */     }
/*     */     
/* 403 */     return false;
/*     */   }
/*     */   
/*     */   public static List<String> getPlatformHosts()
/*     */   {
/* 408 */     return listPlatformHosts;
/*     */   }
/*     */   
/*     */   public static void addPlatformHost(String host) {
/* 412 */     List<String> platformHosts = getPlatformHosts();
/* 413 */     host = host.toLowerCase();
/*     */     
/* 415 */     if (!platformHosts.contains(host)) {
/* 416 */       platformHosts.add(host);
/* 417 */       mapPlatformTrackerTorrents.clear();
/*     */     }
/*     */   }
/*     */   
/*     */   public static boolean isPlatformHost(String host) {
/* 422 */     Object[] domains = getPlatformHosts().toArray();
/*     */     
/* 424 */     host = host.toLowerCase();
/*     */     
/* 426 */     for (int i = 0; i < domains.length; i++)
/*     */     {
/* 428 */       String domain = (String)domains[i];
/*     */       
/* 430 */       if (domain.equals(host))
/*     */       {
/* 432 */         return true;
/*     */       }
/*     */       
/* 435 */       if (host.endsWith("." + domain))
/*     */       {
/* 437 */         return true;
/*     */       }
/*     */     }
/*     */     
/* 441 */     if (Constants.isCVSVersion())
/*     */     {
/*     */       try
/*     */       {
/*     */ 
/* 446 */         InetAddress ia = InetAddress.getByName(host);
/*     */         
/* 448 */         return (ia.isLoopbackAddress()) || (ia.isLinkLocalAddress()) || (ia.isSiteLocalAddress());
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/*     */ 
/* 454 */     return false;
/*     */   }
/*     */   
/*     */   public static boolean isPlatformTracker(TOTorrent torrent) {
/*     */     try {
/* 459 */       if (torrent == null)
/*     */       {
/* 461 */         return false;
/*     */       }
/*     */       
/* 464 */       Object oCache = mapPlatformTrackerTorrents.get(torrent);
/* 465 */       if ((oCache instanceof Boolean)) {
/* 466 */         return ((Boolean)oCache).booleanValue();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 472 */       URL announceURL = torrent.getAnnounceURL();
/*     */       
/* 474 */       if (announceURL != null)
/*     */       {
/* 476 */         if (!isPlatformHost(announceURL.getHost()))
/*     */         {
/* 478 */           mapPlatformTrackerTorrents.put(torrent, Boolean.FALSE);
/* 479 */           return false;
/*     */         }
/*     */       }
/*     */       
/* 483 */       TOTorrentAnnounceURLSet[] sets = torrent.getAnnounceURLGroup().getAnnounceURLSets();
/*     */       
/* 485 */       for (int i = 0; i < sets.length; i++)
/*     */       {
/* 487 */         URL[] urls = sets[i].getAnnounceURLs();
/*     */         
/* 489 */         for (int j = 0; j < urls.length; j++)
/*     */         {
/* 491 */           if (!isPlatformHost(urls[j].getHost()))
/*     */           {
/* 493 */             mapPlatformTrackerTorrents.put(torrent, Boolean.FALSE);
/* 494 */             return false;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 499 */       boolean b = announceURL != null;
/* 500 */       mapPlatformTrackerTorrents.put(torrent, Boolean.valueOf(b));
/* 501 */       return b;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 505 */       Debug.printStackTrace(e);
/*     */       
/* 507 */       mapPlatformTrackerTorrents.put(torrent, Boolean.FALSE); }
/* 508 */     return false;
/*     */   }
/*     */   
/*     */   public static boolean isPlatformTracker(Torrent torrent)
/*     */   {
/* 513 */     if ((torrent instanceof TorrentImpl)) {
/* 514 */       return isPlatformTracker(((TorrentImpl)torrent).getTorrent());
/*     */     }
/* 516 */     return false;
/*     */   }
/*     */   
/*     */   public static boolean isAdvancedViewOnly(DownloadManager dm) {
/* 520 */     Boolean oisUpdate = (Boolean)dm.getUserData("isAdvancedViewOnly");
/* 521 */     if (oisUpdate != null) {
/* 522 */       return oisUpdate.booleanValue();
/*     */     }
/*     */     
/* 525 */     boolean advanced_view = true;
/*     */     
/* 527 */     if (!dm.getDownloadState().getFlag(16L))
/*     */     {
/* 529 */       TOTorrent torrent = dm.getTorrent();
/* 530 */       if (torrent == null) {
/* 531 */         advanced_view = false;
/*     */       } else {
/* 533 */         URL announceURL = torrent.getAnnounceURL();
/*     */         
/* 535 */         if (announceURL != null) {
/* 536 */           String host = announceURL.getHost();
/*     */           
/* 538 */           if ((!host.endsWith(".aelitis.com")) && (!host.endsWith(".vuze.com"))) {
/* 539 */             advanced_view = false;
/*     */           }
/*     */         }
/*     */         
/* 543 */         if (advanced_view) {
/* 544 */           TOTorrentAnnounceURLSet[] sets = torrent.getAnnounceURLGroup().getAnnounceURLSets();
/*     */           
/* 546 */           for (int i = 0; i < sets.length; i++)
/*     */           {
/* 548 */             URL[] urls = sets[i].getAnnounceURLs();
/*     */             
/* 550 */             for (int j = 0; j < urls.length; j++)
/*     */             {
/* 552 */               String host = urls[j].getHost();
/*     */               
/* 554 */               if ((!host.endsWith(".aelitis.com")) && (!host.endsWith(".vuze.com"))) {
/* 555 */                 advanced_view = false;
/* 556 */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 564 */     dm.setUserData("isAdvancedViewOnly", Boolean.valueOf(advanced_view));
/*     */     
/* 566 */     return advanced_view;
/*     */   }
/*     */   
/*     */   public static boolean isContentProgressive(TOTorrent torrent) {
/* 570 */     return getContentMapLong(torrent, "Progressive", 0L) == 1L;
/*     */   }
/*     */   
/*     */   public static long getContentStreamSpeedBps(TOTorrent torrent) {
/* 574 */     return getContentMapLong(torrent, "Speed Bps", 0L);
/*     */   }
/*     */   
/*     */   public static long getContentMinimumSpeedBps(TOTorrent torrent) {
/* 578 */     return getContentMapLong(torrent, "Min Speed Bps", 102400L);
/*     */   }
/*     */   
/*     */   public static long getExpiresOn(TOTorrent torrent) {
/* 582 */     Map mapContent = getContentMap(torrent);
/* 583 */     Long l = (Long)mapContent.get("Expires On");
/* 584 */     if (l == null) {
/* 585 */       return 0L;
/*     */     }
/* 587 */     return l.longValue();
/*     */   }
/*     */   
/*     */   public static int getContentPrimaryFileIndex(TOTorrent torrent) {
/* 591 */     return (int)getContentMapLong(torrent, "Primary File Index", -1L);
/*     */   }
/*     */   
/*     */   public static void setContentPrimaryFileIndex(TOTorrent torrent, int index) {
/* 595 */     setContentMapLong(torrent, "Primary File Index", index);
/*     */   }
/*     */   
/*     */   private static long getContentVideoWidth(TOTorrent torrent) {
/* 599 */     return getContentMapLong(torrent, "Video Width", -1L);
/*     */   }
/*     */   
/*     */   private static long getContentVideoHeight(TOTorrent torrent) {
/* 603 */     return getContentMapLong(torrent, "Video Height", -1L);
/*     */   }
/*     */   
/*     */   public static long getContentVideoRunningTime(TOTorrent torrent) {
/* 607 */     return getContentMapLong(torrent, "Running Time", -1L);
/*     */   }
/*     */   
/*     */   public static long getContentDurationMillis(TOTorrent torrent) {
/* 611 */     return getContentMapLong(torrent, "Duration", -1L);
/*     */   }
/*     */   
/*     */   public static void setContentDurationMillis(TOTorrent torrent, long millis) {
/* 615 */     setContentMapLong(torrent, "Duration", millis);
/*     */   }
/*     */   
/*     */   public static int[] getContentVideoResolution(TOTorrent torrent) {
/* 619 */     long width = getContentVideoWidth(torrent);
/* 620 */     if (width <= 0L) {
/* 621 */       return null;
/*     */     }
/* 623 */     long height = getContentVideoHeight(torrent);
/* 624 */     if (height <= 0L) {
/* 625 */       return null;
/*     */     }
/* 627 */     return new int[] { (int)width, (int)height };
/*     */   }
/*     */   
/*     */   public static void log(String str)
/*     */   {
/* 632 */     AEDiagnosticsLogger diag_logger = AEDiagnostics.getLogger("v3.MD");
/* 633 */     diag_logger.log(str);
/* 634 */     if (DEBUG_CACHING) {
/* 635 */       System.out.println(Thread.currentThread().getName() + "|" + System.currentTimeMillis() + "] " + str);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void log(TOTorrent torrent, String string)
/*     */   {
/* 647 */     String hash = "";
/*     */     try {
/* 649 */       hash = torrent.getHashWrapper().toBase32String();
/*     */     }
/*     */     catch (Exception e) {}
/* 652 */     log(hash + "] " + string);
/*     */   }
/*     */   
/*     */   public static boolean embeddedPlayerAvail()
/*     */   {
/* 657 */     if (embeddedPlayerAvail) {
/* 658 */       return true;
/*     */     }
/*     */     try
/*     */     {
/* 662 */       PluginInterface pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID("azemp", true);
/*     */       
/* 664 */       if (pi != null) {
/* 665 */         embeddedPlayerAvail = true;
/*     */       }
/*     */     }
/*     */     catch (Throwable e1) {}
/*     */     
/* 670 */     return embeddedPlayerAvail;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getContentTitle2(DownloadManager dm)
/*     */   {
/* 679 */     if (dm == null) {
/* 680 */       return null;
/*     */     }
/*     */     
/* 683 */     String name = dm.getDownloadState().getDisplayName();
/* 684 */     if ((name == null) || (name.length() == 0)) {
/* 685 */       name = getContentTitle(dm.getTorrent());
/* 686 */       if (name == null) {
/* 687 */         name = dm.getDisplayName();
/*     */       }
/*     */     }
/* 690 */     return name;
/*     */   }
/*     */   
/*     */   public static void setHasBeenOpened(DownloadManager dm, boolean opened) {
/* 694 */     TOTorrent torrent = dm.getTorrent();
/* 695 */     if (torrent == null) {
/* 696 */       return;
/*     */     }
/* 698 */     if (opened == getHasBeenOpened(dm)) {
/* 699 */       return;
/*     */     }
/* 701 */     setContentMapLong(torrent, "Opened", opened ? 1L : 0L);
/* 702 */     writeTorrentIfExists(torrent);
/* 703 */     Object[] array = hasBeenOpenedListeners.toArray();
/* 704 */     for (int i = 0; i < array.length; i++) {
/*     */       try {
/* 706 */         HasBeenOpenedListener l = (HasBeenOpenedListener)array[i];
/* 707 */         l.hasBeenOpenedChanged(dm, opened);
/*     */       } catch (Exception e) {
/* 709 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static boolean getHasBeenOpened(DownloadManager dm) {
/* 715 */     TOTorrent torrent = dm.getTorrent();
/* 716 */     if (torrent == null) {
/* 717 */       return true;
/*     */     }
/* 719 */     boolean opened = getContentMapLong(torrent, "Opened", -1L) > 0L;
/* 720 */     if ((opened) || (isAdvancedViewOnly(dm))) {
/* 721 */       return true;
/*     */     }
/*     */     
/* 724 */     return false;
/*     */   }
/*     */   
/*     */   public static void addHasBeenOpenedListener(HasBeenOpenedListener l) {
/* 728 */     hasBeenOpenedListeners.add(l);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/torrent/PlatformTorrentUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */