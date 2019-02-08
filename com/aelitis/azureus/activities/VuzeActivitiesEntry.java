/*     */ package com.aelitis.azureus.activities;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.torrent.PlatformTorrentUtils;
/*     */ import com.aelitis.azureus.core.util.AZ3Functions.provider.LocalActivityCallback;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnSortObject;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentV3;
/*     */ import com.aelitis.azureus.ui.utils.ImageBytesDownloader;
/*     */ import com.aelitis.azureus.ui.utils.ImageBytesDownloader.ImageDownloaderListener;
/*     */ import com.aelitis.azureus.util.DataSourceUtils;
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import com.aelitis.azureus.util.PlayUtils;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*     */ import org.gudy.azureus2.core3.util.BDecoder;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ public class VuzeActivitiesEntry
/*     */   implements TableColumnSortObject
/*     */ {
/*     */   private String text;
/*     */   private String iconID;
/*     */   private String id;
/*     */   private long timestamp;
/*     */   private String typeID;
/*     */   private String assetHash;
/*     */   private String assetImageURL;
/*     */   private DownloadManager dm;
/*     */   public Object urlInfo;
/*     */   public TableColumnCore tableColumn;
/*     */   private byte[] imageBytes;
/*  81 */   private boolean showThumb = true;
/*     */   
/*     */   private String torrentName;
/*     */   
/*     */   private TOTorrent torrent;
/*     */   
/*     */   private boolean playable;
/*     */   
/*     */   private long readOn;
/*     */   
/*     */   private String[] actions;
/*     */   
/*     */   private String callback_class;
/*     */   private Map<String, String> callback_data;
/*     */   private boolean viewed;
/*  96 */   private GlobalManager gm = null;
/*     */   
/*     */   public VuzeActivitiesEntry(long timestamp, String text, String typeID) {
/*  99 */     setText(text);
/* 100 */     this.timestamp = timestamp;
/* 101 */     setTypeID(typeID, true);
/*     */   }
/*     */   
/*     */   public VuzeActivitiesEntry(long timestamp, String text, String icon, String id, String typeID, String assetHash)
/*     */   {
/* 106 */     this.timestamp = timestamp;
/* 107 */     setText(text);
/* 108 */     setIconID(icon);
/* 109 */     setID(id);
/* 110 */     setTypeID(typeID, true);
/* 111 */     setAssetHash(assetHash);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public VuzeActivitiesEntry()
/*     */   {
/* 118 */     this.timestamp = SystemTime.getCurrentTime();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void updateFrom(VuzeActivitiesEntry other)
/*     */   {
/* 125 */     this.text = other.text;
/* 126 */     this.iconID = other.iconID;
/* 127 */     this.id = other.id;
/* 128 */     this.timestamp = other.timestamp;
/* 129 */     this.typeID = other.typeID;
/* 130 */     this.assetHash = other.assetHash;
/* 131 */     this.assetImageURL = other.assetImageURL;
/* 132 */     this.dm = other.dm;
/* 133 */     this.urlInfo = other.urlInfo;
/* 134 */     this.tableColumn = other.tableColumn;
/* 135 */     this.imageBytes = other.imageBytes;
/* 136 */     this.showThumb = other.showThumb;
/* 137 */     this.torrentName = other.torrentName;
/* 138 */     this.torrent = other.torrent;
/* 139 */     this.playable = other.playable;
/* 140 */     this.readOn = 0L;
/* 141 */     this.actions = other.actions;
/* 142 */     this.callback_class = other.callback_class;
/* 143 */     this.callback_data = other.callback_data;
/* 144 */     this.viewed = false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void loadFromExternalMap(Map<?, ?> platformEntry)
/*     */   {
/* 151 */     this.timestamp = (SystemTime.getCurrentTime() - MapUtils.getMapLong(platformEntry, "age-ms", 0L));
/*     */     
/* 153 */     setIconID(MapUtils.getMapString(platformEntry, "icon-url", MapUtils.getMapString(platformEntry, "icon-id", null)));
/*     */     
/* 155 */     setTypeID(MapUtils.getMapString(platformEntry, "type-id", null), true);
/* 156 */     setAssetHash(MapUtils.getMapString(platformEntry, "related-asset-hash", null));
/*     */     
/* 158 */     setAssetImageURL(MapUtils.getMapString(platformEntry, "related-image-url", null));
/*     */     
/* 160 */     setTorrentName(MapUtils.getMapString(platformEntry, "related-asset-name", null));
/*     */     
/* 162 */     setReadOn(MapUtils.getMapLong(platformEntry, "readOn", 0L));
/* 163 */     loadCommonFromMap(platformEntry);
/*     */   }
/*     */   
/*     */   public void loadFromInternalMap(Map<?, ?> map) {
/* 167 */     this.timestamp = MapUtils.getMapLong(map, "timestamp", 0L);
/* 168 */     if (this.timestamp == 0L) {
/* 169 */       this.timestamp = SystemTime.getCurrentTime();
/*     */     }
/* 171 */     setAssetHash(MapUtils.getMapString(map, "assetHash", null));
/* 172 */     setIconIDRaw(MapUtils.getMapString(map, "icon", null));
/* 173 */     setTypeID(MapUtils.getMapString(map, "typeID", null), true);
/* 174 */     setShowThumb(MapUtils.getMapLong(map, "showThumb", 1L) == 1L);
/* 175 */     setAssetImageURL(MapUtils.getMapString(map, "assetImageURL", null));
/* 176 */     setImageBytes(MapUtils.getMapByteArray(map, "imageBytes", null));
/* 177 */     setReadOn(MapUtils.getMapLong(map, "readOn", SystemTime.getCurrentTime()));
/* 178 */     setActions(MapUtils.getMapStringArray(map, "actions", null));
/*     */     
/* 180 */     this.callback_class = MapUtils.getMapString(map, "cb_class", null);
/* 181 */     this.callback_data = BDecoder.decodeStrings((Map)map.get("cb_data"));
/*     */     
/* 183 */     this.viewed = MapUtils.getMapBoolean(map, "viewed", false);
/*     */     
/* 185 */     loadCommonFromMap(map);
/*     */   }
/*     */   
/*     */   public void loadCommonFromMap(Map<?, ?> map) {
/* 189 */     if (!this.playable) {
/* 190 */       setPlayable(MapUtils.getMapBoolean(map, "playable", false));
/*     */     }
/* 192 */     setID(MapUtils.getMapString(map, "id", null));
/* 193 */     setText(MapUtils.getMapString(map, "text", null));
/* 194 */     Map<?, ?> torrentMap = MapUtils.getMapMap(map, "torrent", null);
/* 195 */     if (torrentMap != null) {
/* 196 */       TOTorrent torrent = null;
/*     */       try {
/* 198 */         torrent = TOTorrentFactory.deserialiseFromMap(torrentMap);
/* 199 */         setTorrent(torrent);
/*     */       }
/*     */       catch (TOTorrentException e) {}
/*     */     }
/* 203 */     if ((this.dm == null) && (this.torrentName == null)) {
/* 204 */       setTorrentName(MapUtils.getMapString(map, "torrent-name", null));
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean equals(Object obj) {
/* 209 */     if (((obj instanceof VuzeActivitiesEntry)) && (this.id != null)) {
/* 210 */       return this.id.equals(((VuzeActivitiesEntry)obj).id);
/*     */     }
/* 212 */     return super.equals(obj);
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 218 */     if (this.id == null) {
/* 219 */       return 0;
/*     */     }
/* 221 */     return this.id.hashCode();
/*     */   }
/*     */   
/*     */   public int compareTo(Object obj) {
/* 225 */     if ((obj instanceof VuzeActivitiesEntry)) {
/* 226 */       VuzeActivitiesEntry otherEntry = (VuzeActivitiesEntry)obj;
/*     */       
/* 228 */       long x = this.timestamp - otherEntry.timestamp;
/* 229 */       return x > 0L ? 1 : x == 0L ? 0 : -1;
/*     */     }
/*     */     
/* 232 */     return 1;
/*     */   }
/*     */   
/*     */   public void setAssetImageURL(String url) {
/* 236 */     if ((url == null) && (this.assetImageURL == null)) {
/* 237 */       return;
/*     */     }
/* 239 */     if ((url == null) || (url.length() == 0)) {
/* 240 */       this.assetImageURL = null;
/* 241 */       VuzeActivitiesManager.triggerEntryChanged(this);
/* 242 */       return;
/*     */     }
/* 244 */     if (url.equals(this.assetImageURL)) {
/* 245 */       return;
/*     */     }
/*     */     
/* 248 */     this.assetImageURL = url;
/* 249 */     ImageBytesDownloader.loadImage(url, new ImageBytesDownloader.ImageDownloaderListener() {
/*     */       public void imageDownloaded(byte[] image) {
/* 251 */         VuzeActivitiesEntry.this.setImageBytes(image);
/* 252 */         VuzeActivitiesManager.triggerEntryChanged(VuzeActivitiesEntry.this);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public String getAssetImageURL() {
/* 258 */     return this.assetImageURL;
/*     */   }
/*     */   
/*     */   public Map<String, Object> toDeletedMap() {
/* 262 */     Map<String, Object> map = new HashMap();
/* 263 */     map.put("timestamp", new Long(this.timestamp));
/* 264 */     map.put("id", this.id);
/* 265 */     return map;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setActions(String[] _actions)
/*     */   {
/* 272 */     this.actions = _actions;
/*     */   }
/*     */   
/*     */ 
/*     */   public String[] getActions()
/*     */   {
/* 278 */     return this.actions == null ? new String[0] : this.actions;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean allowReAdd()
/*     */   {
/* 284 */     return (this.callback_data != null) && (this.callback_data.containsKey("allowReAdd"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setCallback(Class<? extends AZ3Functions.provider.LocalActivityCallback> _callback, Map<String, String> _callback_data)
/*     */   {
/* 292 */     this.callback_class = (_callback == null ? null : _callback.getName());
/* 293 */     this.callback_data = _callback_data;
/*     */   }
/*     */   
/*     */ 
/*     */   public void invokeCallback(String action)
/*     */   {
/*     */     try
/*     */     {
/* 301 */       getClass();Class<? extends AZ3Functions.provider.LocalActivityCallback> cb = Class.forName(this.callback_class);
/*     */       
/* 303 */       ((AZ3Functions.provider.LocalActivityCallback)cb.newInstance()).actionSelected(action, this.callback_data);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 307 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map<String, Object> toMap()
/*     */   {
/* 316 */     Map<String, Object> map = new HashMap();
/* 317 */     map.put("timestamp", new Long(this.timestamp));
/* 318 */     if (this.assetHash != null) {
/* 319 */       map.put("assetHash", this.assetHash);
/*     */     }
/* 321 */     map.put("icon", getIconID());
/* 322 */     map.put("id", this.id);
/* 323 */     map.put("text", getText());
/* 324 */     map.put("typeID", getTypeID());
/* 325 */     map.put("assetImageURL", this.assetImageURL);
/* 326 */     map.put("showThumb", new Long(getShowThumb() ? 1L : 0L));
/* 327 */     if (this.imageBytes != null) {
/* 328 */       map.put("imageBytes", this.imageBytes);
/* 329 */     } else if (this.dm != null) {
/* 330 */       byte[] thumbnail = PlatformTorrentUtils.getContentThumbnail(this.dm.getTorrent());
/* 331 */       if (thumbnail != null) {
/* 332 */         map.put("imageBytes", thumbnail);
/*     */       }
/*     */     }
/*     */     
/* 336 */     if ((this.torrent != null) && ((this.dm == null) || (this.assetHash == null)))
/*     */     {
/*     */       try
/*     */       {
/* 340 */         Map torrent_map = this.torrent.serialiseToMap();
/*     */         
/* 342 */         TOTorrent torrent_to_send = TOTorrentFactory.deserialiseFromMap(torrent_map);
/*     */         
/* 344 */         Map<?, ?> vuze_map = (Map)torrent_map.get("vuze");
/*     */         
/*     */ 
/*     */ 
/* 348 */         torrent_to_send.removeAdditionalProperties();
/*     */         
/* 350 */         torrent_map = torrent_to_send.serialiseToMap();
/*     */         
/* 352 */         if (vuze_map != null)
/*     */         {
/* 354 */           torrent_map.put("vuze", vuze_map);
/*     */         }
/*     */         
/* 357 */         map.put("torrent", torrent_map);
/*     */       } catch (TOTorrentException e) {
/* 359 */         Debug.outNoStack("VuzeActivityEntry.toMap: " + e.toString());
/*     */       }
/*     */     }
/* 362 */     if (this.torrentName != null) {
/* 363 */       map.put("torrent-name", this.torrentName);
/*     */     }
/*     */     
/* 366 */     if (this.playable) {
/* 367 */       map.put("playable", new Long(this.playable ? 1L : 0L));
/*     */     }
/*     */     
/* 370 */     map.put("readOn", new Long(this.readOn));
/*     */     
/* 372 */     if ((this.actions != null) && (this.actions.length > 0)) {
/* 373 */       List<String> list = Arrays.asList(this.actions);
/* 374 */       map.put("actions", list);
/*     */     }
/* 376 */     if (this.callback_class != null) {
/* 377 */       map.put("cb_class", this.callback_class);
/*     */     }
/* 379 */     if (this.callback_data != null) {
/* 380 */       map.put("cb_data", this.callback_data);
/*     */     }
/*     */     
/* 383 */     map.put("viewed", Integer.valueOf(this.viewed ? 1 : 0));
/*     */     
/* 385 */     return map;
/*     */   }
/*     */   
/*     */   public long getTimestamp() {
/* 389 */     return this.timestamp;
/*     */   }
/*     */   
/*     */   public void setTimestamp(long timestamp) {
/* 393 */     if (this.timestamp == timestamp) {
/* 394 */       return;
/*     */     }
/* 396 */     this.timestamp = timestamp;
/* 397 */     if (this.tableColumn != null) {
/* 398 */       this.tableColumn.setLastSortValueChange(SystemTime.getCurrentTime());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setTypeID(String typeID, boolean autoSetIcon)
/*     */   {
/* 406 */     this.typeID = typeID;
/* 407 */     if ((getIconID() == null) && (typeID != null)) {
/* 408 */       setIconID("image.vuze-entry." + typeID.toLowerCase());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getTypeID()
/*     */   {
/* 416 */     return this.typeID;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setIconID(String iconID)
/*     */   {
/* 423 */     if ((iconID != null) && (!iconID.contains("image.")) && (!iconID.startsWith("http")))
/*     */     {
/* 425 */       iconID = "image.vuze-entry." + iconID;
/*     */     }
/* 427 */     this.iconID = iconID;
/*     */   }
/*     */   
/*     */   public void setIconIDRaw(String iconID) {
/* 431 */     this.iconID = iconID;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getIconID()
/*     */   {
/* 438 */     return this.iconID;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setText(String text)
/*     */   {
/* 445 */     this.text = text;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getText()
/*     */   {
/* 452 */     return this.text;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setID(String id)
/*     */   {
/* 459 */     this.id = id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getID()
/*     */   {
/* 466 */     return this.id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setAssetHash(String assetHash)
/*     */   {
/* 473 */     this.assetHash = assetHash;
/* 474 */     if (assetHash != null) {
/*     */       try {
/* 476 */         if (this.gm == null) {
/* 477 */           this.gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/*     */         }
/* 479 */         setDownloadManager(this.gm.getDownloadManager(new HashWrapper(Base32.decode(assetHash))));
/*     */       }
/*     */       catch (Exception e) {
/* 482 */         setDownloadManager(null);
/* 483 */         Debug.out("Core not ready", e);
/*     */       }
/*     */     } else {
/* 486 */       setDownloadManager(null);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getAssetHash()
/*     */   {
/* 494 */     return this.assetHash;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDownloadManager(DownloadManager dm)
/*     */   {
/* 501 */     if (this.dm == dm) {
/* 502 */       return;
/*     */     }
/* 504 */     if (this.gm == null) {
/*     */       try {
/* 506 */         this.gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/*     */       }
/*     */       catch (Exception e) {}
/*     */     }
/*     */     
/*     */ 
/* 512 */     this.dm = dm;
/* 513 */     if (dm != null) {
/* 514 */       setTorrent(dm.getTorrent());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DownloadManager getDownloadManger()
/*     */   {
/* 522 */     if ((this.gm != null) && (!this.gm.contains(this.dm))) {
/* 523 */       setDownloadManager(null);
/* 524 */       return null;
/*     */     }
/* 526 */     return this.dm;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setImageBytes(byte[] imageBytes)
/*     */   {
/* 533 */     this.imageBytes = imageBytes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getImageBytes()
/*     */   {
/* 540 */     return this.imageBytes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setShowThumb(boolean showThumb)
/*     */   {
/* 547 */     this.showThumb = showThumb;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean getShowThumb()
/*     */   {
/* 554 */     return this.showThumb;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TOTorrent getTorrent()
/*     */   {
/* 566 */     return this.torrent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTorrent(TOTorrent torrent)
/*     */   {
/* 578 */     this.torrent = torrent;
/*     */     try
/*     */     {
/* 581 */       this.assetHash = torrent.getHashWrapper().toBase32String();
/*     */     }
/*     */     catch (Exception e) {}
/*     */   }
/*     */   
/*     */   public String getTorrentName() {
/* 587 */     if (this.torrentName == null) {
/* 588 */       if (this.dm != null) {
/* 589 */         return PlatformTorrentUtils.getContentTitle2(this.dm);
/*     */       }
/* 591 */       if (this.torrent != null) {
/* 592 */         return TorrentUtils.getLocalisedName(this.torrent);
/*     */       }
/*     */     }
/* 595 */     return this.torrentName;
/*     */   }
/*     */   
/*     */   public void setTorrentName(String torrentName) {
/* 599 */     this.torrentName = torrentName;
/*     */   }
/*     */   
/*     */   public SelectedContentV3 createSelectedContentObject()
/*     */     throws Exception
/*     */   {
/* 605 */     boolean ourContent = DataSourceUtils.isPlatformContent(this);
/*     */     
/* 607 */     SelectedContentV3 sc = new SelectedContentV3();
/* 608 */     if (this.assetHash == null)
/*     */     {
/* 610 */       return sc;
/*     */     }
/*     */     
/* 613 */     this.dm = getDownloadManger();
/* 614 */     if (this.dm != null) {
/* 615 */       sc.setDisplayName(PlatformTorrentUtils.getContentTitle2(this.dm));
/* 616 */       sc.setDownloadManager(this.dm);
/* 617 */       return sc;
/*     */     }
/* 619 */     if (this.torrent != null) {
/* 620 */       sc.setTorrent(this.torrent);
/*     */     }
/*     */     
/*     */ 
/* 624 */     sc.setDisplayName(getTorrentName());
/* 625 */     if (sc.getDisplayName() == null) {
/* 626 */       TOTorrent torrent = getTorrent();
/* 627 */       if (torrent != null) {
/* 628 */         sc.setDisplayName(TorrentUtils.getLocalisedName(torrent));
/* 629 */         sc.setHash(torrent.getHashWrapper().toBase32String(), ourContent);
/*     */       }
/*     */     }
/*     */     
/* 633 */     if (sc.getHash() == null)
/*     */     {
/* 635 */       if (this.assetHash != null)
/*     */       {
/* 637 */         sc.setHash(this.assetHash, true);
/*     */       }
/*     */     }
/*     */     
/* 641 */     sc.setThumbURL(this.assetImageURL);
/* 642 */     sc.setImageBytes(this.imageBytes);
/*     */     
/* 644 */     return sc;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isPlayable(boolean blocking)
/*     */   {
/* 650 */     if (this.playable) {
/* 651 */       return true;
/*     */     }
/*     */     
/* 654 */     return PlayUtils.canPlayDS(DataSourceUtils.getTorrent(this), -1, blocking);
/*     */   }
/*     */   
/*     */   public void setPlayable(boolean playable) {
/* 658 */     this.playable = playable;
/*     */   }
/*     */   
/*     */   public long getReadOn() {
/* 662 */     return this.readOn;
/*     */   }
/*     */   
/*     */   public void setReadOn(long readOn) {
/* 666 */     if (this.readOn == readOn) {
/* 667 */       return;
/*     */     }
/* 669 */     this.readOn = readOn;
/* 670 */     VuzeActivitiesManager.triggerEntryChanged(this);
/*     */   }
/*     */   
/*     */   public void setRead(boolean read) {
/* 674 */     long now = SystemTime.getCurrentTime();
/* 675 */     if (read) {
/* 676 */       setReadOn(now);
/*     */     } else {
/* 678 */       setReadOn(now * -1L);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isRead() {
/* 683 */     return this.readOn > 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setViewed()
/*     */   {
/* 689 */     if (!this.viewed) {
/* 690 */       this.viewed = true;
/* 691 */       VuzeActivitiesManager.triggerEntryChanged(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getViewed()
/*     */   {
/* 698 */     return this.viewed;
/*     */   }
/*     */   
/*     */   public boolean canFlipRead() {
/* 702 */     long ofs = SystemTime.getOffsetTime(-300L);
/* 703 */     if (this.readOn > 0L) {
/* 704 */       return ofs > this.readOn;
/*     */     }
/* 706 */     return ofs > -1L * this.readOn;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/activities/VuzeActivitiesEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */