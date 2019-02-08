/*     */ package com.aelitis.azureus.core.content;
/*     */ 
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.plugins.download.Download;
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
/*     */ public abstract class RelatedContent
/*     */ {
/*     */   public static final int VERSION_NA = -1;
/*     */   public static final int VERSION_INITIAL = 0;
/*     */   public static final int VERSION_BETTER_SCRAPE = 1;
/*  36 */   public static final String[] NO_TAGS = new String[0];
/*     */   
/*     */ 
/*     */   private final String title;
/*     */   
/*     */ 
/*     */   private final byte[] hash;
/*     */   
/*     */ 
/*     */   private final String tracker;
/*     */   
/*     */ 
/*     */   private final long size;
/*     */   
/*     */ 
/*     */   private int version;
/*     */   
/*     */   private int date;
/*     */   
/*     */   private int seeds_leechers;
/*     */   
/*     */   private byte content_network;
/*     */   
/*     */   private byte[] related_to_hash;
/*     */   
/*     */   private byte[] tracker_keys;
/*     */   
/*     */   private byte[] ws_keys;
/*     */   
/*     */   private String[] tags;
/*     */   
/*     */   private byte nets;
/*     */   
/*     */   private long changed_locally_on;
/*     */   
/*     */ 
/*     */   public RelatedContent(int _version, byte[] _related_to_hash, String _title, byte[] _hash, String _tracker, byte[] _tracker_keys, byte[] _ws_keys, String[] _tags, byte _nets, long _size, int _date, int _seeds_leechers, byte _cnet)
/*     */   {
/*  74 */     this.version = _version;
/*  75 */     this.related_to_hash = _related_to_hash;
/*  76 */     this.title = _title;
/*  77 */     this.hash = _hash;
/*  78 */     this.tracker = _tracker;
/*  79 */     this.tracker_keys = _tracker_keys;
/*  80 */     this.ws_keys = _ws_keys;
/*  81 */     this.tags = _tags;
/*  82 */     this.nets = _nets;
/*  83 */     this.size = _size;
/*  84 */     this.date = _date;
/*  85 */     this.seeds_leechers = _seeds_leechers;
/*  86 */     this.content_network = _cnet;
/*  87 */     setChangedLocallyOn(0L);
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
/*     */   public RelatedContent(String _title, byte[] _hash, String _tracker, long _size, int _date, int _seeds_leechers, byte _cnet)
/*     */   {
/* 102 */     this(-1, _title, _hash, _tracker, null, null, null, (byte)1, _size, _date, _seeds_leechers, _cnet);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public RelatedContent(String _title, byte[] _hash, String _tracker, byte[] _tracker_keys, byte[] _ws_keys, String[] _tags, byte _nets, long _size, int _date, int _seeds_leechers, byte _cnet)
/*     */   {
/* 121 */     this(-1, _title, _hash, _tracker, _tracker_keys, _ws_keys, _tags, _nets, _size, _date, _seeds_leechers, _cnet);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public RelatedContent(int _version, String _title, byte[] _hash, String _tracker, byte[] _tracker_keys, byte[] _ws_keys, String[] _tags, byte _nets, long _size, int _date, int _seeds_leechers, byte _cnet)
/*     */   {
/* 139 */     this.version = _version;
/* 140 */     this.title = _title;
/* 141 */     this.hash = _hash;
/* 142 */     this.tracker = _tracker;
/* 143 */     this.tracker_keys = _tracker_keys;
/* 144 */     this.ws_keys = _ws_keys;
/* 145 */     this.tags = _tags;
/* 146 */     this.nets = _nets;
/* 147 */     this.size = _size;
/* 148 */     this.date = _date;
/* 149 */     this.seeds_leechers = _seeds_leechers;
/* 150 */     this.content_network = _cnet;
/* 151 */     setChangedLocallyOn(0L);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getVersion()
/*     */   {
/* 157 */     return this.version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setVersion(int _version)
/*     */   {
/* 164 */     this.version = _version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setRelatedToHash(byte[] h)
/*     */   {
/* 171 */     this.related_to_hash = h;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getRelatedToHash()
/*     */   {
/* 180 */     return this.related_to_hash;
/*     */   }
/*     */   
/*     */ 
/*     */   public abstract Download getRelatedToDownload();
/*     */   
/*     */ 
/*     */   public String getTitle()
/*     */   {
/* 189 */     return this.title;
/*     */   }
/*     */   
/*     */ 
/*     */   public abstract int getRank();
/*     */   
/*     */ 
/*     */   public byte[] getHash()
/*     */   {
/* 198 */     return this.hash;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract int getLevel();
/*     */   
/*     */ 
/*     */   public abstract boolean isUnread();
/*     */   
/*     */ 
/*     */   public abstract void setUnread(boolean paramBoolean);
/*     */   
/*     */ 
/*     */   public abstract int getLastSeenSecs();
/*     */   
/*     */ 
/*     */   public String getTracker()
/*     */   {
/* 217 */     return this.tracker;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getTrackerKeys()
/*     */   {
/* 223 */     return this.tracker_keys;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getWebSeedKeys()
/*     */   {
/* 229 */     return this.ws_keys;
/*     */   }
/*     */   
/*     */ 
/*     */   public String[] getTags()
/*     */   {
/* 235 */     return this.tags == null ? NO_TAGS : this.tags;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setTags(String[] _tags)
/*     */   {
/* 242 */     this.tags = _tags;
/* 243 */     setChangedLocallyOn(0L);
/*     */   }
/*     */   
/*     */ 
/*     */   public String[] getNetworks()
/*     */   {
/* 249 */     return RelatedContentManager.convertNetworks(this.nets);
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte getNetworksInternal()
/*     */   {
/* 255 */     return this.nets;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setNetworksInternal(byte n)
/*     */   {
/* 262 */     this.nets = n;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSize()
/*     */   {
/* 268 */     return this.size;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getPublishDate()
/*     */   {
/* 274 */     return this.date * 60 * 60 * 1000L;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getDateHours()
/*     */   {
/* 280 */     return this.date;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setDateHours(int _date)
/*     */   {
/* 287 */     this.date = _date;
/* 288 */     setChangedLocallyOn(0L);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLeechers()
/*     */   {
/* 294 */     if (this.seeds_leechers == -1)
/*     */     {
/* 296 */       return -1;
/*     */     }
/*     */     
/* 299 */     return this.seeds_leechers & 0xFFFF;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSeeds()
/*     */   {
/* 305 */     if (this.seeds_leechers == -1)
/*     */     {
/* 307 */       return -1;
/*     */     }
/*     */     
/* 310 */     return this.seeds_leechers >> 16 & 0xFFFF;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getSeedsLeechers()
/*     */   {
/* 316 */     return this.seeds_leechers;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setSeedsLeechers(int _sl)
/*     */   {
/* 323 */     this.seeds_leechers = _sl;
/* 324 */     setChangedLocallyOn(0L);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getContentNetwork()
/*     */   {
/* 330 */     return (this.content_network & 0xFF) == 255 ? -1L : this.content_network & 0xFF;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setContentNetwork(long cnet)
/*     */   {
/* 337 */     this.content_network = ((byte)(int)cnet);
/* 338 */     setChangedLocallyOn(0L);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getChangedLocallyOn()
/*     */   {
/* 344 */     return this.changed_locally_on;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setChangedLocallyOn(long _changed_locally_on)
/*     */   {
/* 354 */     this.changed_locally_on = (_changed_locally_on == 0L ? SystemTime.getCurrentTime() : _changed_locally_on);
/*     */   }
/*     */   
/*     */ 
/*     */   public abstract void delete();
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 363 */     return "title=" + this.title + ", ver=" + this.version + ", hash=" + (this.hash == null ? "null" : Base32.encode(this.hash)) + ", tracker=" + this.tracker + ", date=" + this.date + ", sl=" + this.seeds_leechers + ", cnet=" + this.content_network + ", nets=" + this.nets;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/content/RelatedContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */