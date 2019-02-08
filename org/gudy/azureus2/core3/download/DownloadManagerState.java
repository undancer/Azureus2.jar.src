/*     */ package org.gudy.azureus2.core3.download;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.LinkFileMap;
/*     */ import java.io.File;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.category.Category;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
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
/*     */ public abstract interface DownloadManagerState
/*     */ {
/*     */   public static final String AT_VERSION = "version";
/*     */   public static final String AT_CATEGORY = "category";
/*     */   public static final String AT_NETWORKS = "networks";
/*     */   public static final String AT_USER = "user";
/*     */   public static final String AT_PEER_SOURCES = "peersources";
/*     */   public static final String AT_PEER_SOURCES_DENIED = "peersourcesdenied";
/*     */   public static final String AT_TRACKER_CLIENT_EXTENSIONS = "trackerclientextensions";
/*     */   public static final String AT_FILE_LINKS_DEPRECATED = "filelinks";
/*     */   public static final String AT_FILE_LINKS2 = "filelinks2";
/*     */   public static final String AT_FILE_STORE_TYPES = "storetypes";
/*     */   public static final String AT_FILE_DOWNLOADED = "filedownloaded";
/*     */   public static final String AT_FLAGS = "flags";
/*     */   public static final String AT_PARAMETERS = "parameters";
/*     */   public static final String AT_DISPLAY_NAME = "displayname";
/*     */   public static final String AT_USER_COMMENT = "comment";
/*     */   public static final String AT_RELATIVE_SAVE_PATH = "relativepath";
/*     */   public static final String AT_SECRETS = "secrets";
/*     */   public static final String AT_RESUME_STATE = "resumecomplete";
/*     */   public static final String AT_PRIMARY_FILE = "primaryfile";
/*     */   public static final String AT_PRIMARY_FILE_IDX = "primaryfileidx";
/*     */   public static final String AT_TIME_SINCE_DOWNLOAD = "timesincedl";
/*     */   public static final String AT_TIME_SINCE_UPLOAD = "timesinceul";
/*     */   public static final String AT_AVAIL_BAD_TIME = "badavail";
/*     */   public static final String AT_TIME_STOPPED = "timestopped";
/*     */   public static final String AT_INCOMP_FILE_SUFFIX = "incompfilesuffix";
/*     */   public static final String AT_SCRAPE_CACHE = "scrapecache";
/*     */   public static final String AT_SCRAPE_CACHE_SOURCE = "scsrc";
/*     */   public static final String AT_REORDER_MIN_MB = "reordermb";
/*     */   public static final String AT_MD_INFO_DICT_SIZE = "mdinfodictsize";
/*     */   public static final String AT_FILE_OTHER_HASHES = "fileotherhashes";
/*     */   public static final String AT_CANONICAL_SD_DMAP = "canosavedir";
/*     */   public static final String AT_DND_SUBFOLDER = "dnd_sf";
/*     */   public static final String AT_PEAK_RECEIVE_RATE = "pkdo";
/*     */   public static final String AT_PEAK_SEND_RATE = "pkup";
/*     */   public static final String AT_DL_FILE_ALERTS = "df_alerts";
/*     */   public static final String AT_SHARE_RATIO_PROGRESS = "sr.prog";
/*     */   public static final String AT_FILES_EXPANDED = "file.expand";
/*     */   public static final String AT_MERGED_DATA = "mergedata";
/*     */   public static final String AT_DND_PREFIX = "dnd_pfx";
/*     */   public static final String AT_AGGREGATE_SCRAPE_CACHE = "agsc";
/*     */   public static final String AT_COMPLETE_LAST_TIME = "complt";
/*     */   public static final String AT_LAST_ADDED_TO_ACTIVE_TAG = "last.act.tag";
/*  84 */   public static final Object[][] ATTRIBUTE_DEFAULTS = { { "version", new Integer(-1) }, { "timesincedl", new Integer(-1) }, { "timesinceul", new Integer(-1) }, { "badavail", new Long(-1L) }, { "scrapecache", new Long(-1L) }, { "scsrc", new Integer(0) }, { "reordermb", new Integer(-1) }, { "sr.prog", new Long(0L) } };
/*     */   
/*     */   public static final long FLAG_ONLY_EVER_SEEDED = 1L;
/*     */   
/*     */   public static final long FLAG_SCAN_INCOMPLETE_PIECES = 2L;
/*     */   
/*     */   public static final long FLAG_DISABLE_AUTO_FILE_MOVE = 4L;
/*     */   
/*     */   public static final long FLAG_MOVE_ON_COMPLETION_DONE = 8L;
/*     */   
/*     */   public static final long FLAG_LOW_NOISE = 16L;
/*     */   
/*     */   public static final long FLAG_ALLOW_PERMITTED_PEER_SOURCE_CHANGES = 32L;
/*     */   
/*     */   public static final long FLAG_DO_NOT_DELETE_DATA_ON_REMOVE = 64L;
/*     */   
/*     */   public static final long FLAG_FORCE_DIRECT_DELETE = 128L;
/*     */   
/*     */   public static final long FLAG_DISABLE_IP_FILTER = 256L;
/*     */   
/*     */   public static final long FLAG_METADATA_DOWNLOAD = 512L;
/*     */   
/*     */   public static final long FLAG_ERROR_REPORTED = 2048L;
/*     */   
/*     */   public static final long FLAG_INITIAL_NETWORKS_SET = 4096L;
/*     */   
/*     */   public static final String PARAM_MAX_PEERS = "max.peers";
/*     */   public static final String PARAM_MAX_PEERS_WHEN_SEEDING = "max.peers.when.seeding";
/*     */   public static final String PARAM_MAX_PEERS_WHEN_SEEDING_ENABLED = "max.peers.when.seeding.enabled";
/*     */   public static final String PARAM_MAX_SEEDS = "max.seeds";
/*     */   public static final String PARAM_MAX_UPLOADS = "max.uploads";
/*     */   public static final String PARAM_MAX_UPLOADS_WHEN_SEEDING = "max.uploads.when.seeding";
/*     */   public static final String PARAM_MAX_UPLOADS_WHEN_SEEDING_ENABLED = "max.uploads.when.seeding.enabled";
/*     */   public static final String PARAM_STATS_COUNTED = "stats.counted";
/*     */   public static final String PARAM_DOWNLOAD_ADDED_TIME = "stats.download.added.time";
/*     */   public static final String PARAM_DOWNLOAD_COMPLETED_TIME = "stats.download.completed.time";
/*     */   public static final String PARAM_DOWNLOAD_FILE_COMPLETED_TIME = "stats.download.file.completed.time";
/*     */   public static final String PARAM_DOWNLOAD_LAST_ACTIVE_TIME = "stats.download.last.active.time";
/*     */   public static final String PARAM_MAX_UPLOAD_WHEN_BUSY = "max.upload.when.busy";
/*     */   public static final String PARAM_DND_FLAGS = "dndflags";
/*     */   public static final String PARAM_RANDOM_SEED = "rand";
/*     */   public static final String PARAM_UPLOAD_PRIORITY = "up.pri";
/*     */   public static final String PARAM_MIN_SHARE_RATIO = "sr.min";
/*     */   public static final String PARAM_MAX_SHARE_RATIO = "sr.max";
/*     */   public static final int DEFAULT_MAX_UPLOADS = 4;
/*     */   public static final int MIN_MAX_UPLOADS = 2;
/*     */   public static final int DEFAULT_UPLOAD_PRIORITY = 0;
/* 131 */   public static final Object[][] PARAMETERS = { { "max.peers", new Integer(0) }, { "max.peers.when.seeding", new Integer(0) }, { "max.peers.when.seeding.enabled", Boolean.FALSE }, { "max.seeds", new Integer(0) }, { "max.uploads", new Long(4L) }, { "max.uploads.when.seeding", new Integer(4) }, { "max.uploads.when.seeding.enabled", Boolean.FALSE }, { "stats.counted", Boolean.FALSE }, { "stats.download.added.time", new Long(0L) }, { "stats.download.file.completed.time", new Long(0L) }, { "stats.download.completed.time", new Long(0L) }, { "stats.download.last.active.time", new Long(0L) }, { "max.upload.when.busy", new Long(0L) }, { "dndflags", new Long(0L) }, { "rand", new Long(0L) }, { "up.pri", new Integer(0) }, { "sr.min", new Integer(0) }, { "sr.max", new Integer(0) } };
/*     */   
/*     */   public abstract TOTorrent getTorrent();
/*     */   
/*     */   public abstract DownloadManager getDownloadManager();
/*     */   
/*     */   public abstract File getStateFile();
/*     */   
/*     */   public abstract void setFlag(long paramLong, boolean paramBoolean);
/*     */   
/*     */   public abstract boolean getFlag(long paramLong);
/*     */   
/*     */   public abstract long getFlags();
/*     */   
/*     */   public abstract void setParameterDefault(String paramString);
/*     */   
/*     */   public abstract int getIntParameter(String paramString);
/*     */   
/*     */   public abstract void setIntParameter(String paramString, int paramInt);
/*     */   
/*     */   public abstract long getLongParameter(String paramString);
/*     */   
/*     */   public abstract void setLongParameter(String paramString, long paramLong);
/*     */   
/*     */   public abstract boolean getBooleanParameter(String paramString);
/*     */   
/*     */   public abstract void setBooleanParameter(String paramString, boolean paramBoolean);
/*     */   
/*     */   public abstract void clearResumeData();
/*     */   
/*     */   public abstract Map getResumeData();
/*     */   
/*     */   public abstract void setResumeData(Map paramMap);
/*     */   
/*     */   public abstract boolean isResumeDataComplete();
/*     */   
/*     */   public abstract void clearTrackerResponseCache();
/*     */   
/*     */   public abstract Map getTrackerResponseCache();
/*     */   
/*     */   public abstract void setTrackerResponseCache(Map paramMap);
/*     */   
/*     */   public abstract Category getCategory();
/*     */   
/*     */   public abstract void setCategory(Category paramCategory);
/*     */   
/*     */   public abstract String getDisplayName();
/*     */   
/*     */   public abstract void setDisplayName(String paramString);
/*     */   
/*     */   public abstract String getUserComment();
/*     */   
/*     */   public abstract void setUserComment(String paramString);
/*     */   
/*     */   public abstract String getRelativeSavePath();
/*     */   
/*     */   public abstract void setPrimaryFile(DiskManagerFileInfo paramDiskManagerFileInfo);
/*     */   
/*     */   public abstract DiskManagerFileInfo getPrimaryFile();
/*     */   
/*     */   public abstract String getTrackerClientExtensions();
/*     */   
/*     */   public abstract void setTrackerClientExtensions(String paramString);
/*     */   
/*     */   public abstract String[] getNetworks();
/*     */   
/*     */   public abstract boolean isNetworkEnabled(String paramString);
/*     */   
/*     */   public abstract void setNetworks(String[] paramArrayOfString);
/*     */   
/*     */   public abstract void setNetworkEnabled(String paramString, boolean paramBoolean);
/*     */   
/*     */   public abstract String[] getPeerSources();
/*     */   
/*     */   public abstract boolean isPeerSourcePermitted(String paramString);
/*     */   
/*     */   public abstract void setPeerSourcePermitted(String paramString, boolean paramBoolean);
/*     */   
/*     */   public abstract boolean isPeerSourceEnabled(String paramString);
/*     */   
/*     */   public abstract void setPeerSources(String[] paramArrayOfString);
/*     */   
/*     */   public abstract void setPeerSourceEnabled(String paramString, boolean paramBoolean);
/*     */   
/*     */   public abstract void setFileLink(int paramInt, File paramFile1, File paramFile2);
/*     */   
/*     */   public abstract void setFileLinks(List<Integer> paramList, List<File> paramList1, List<File> paramList2);
/*     */   
/*     */   public abstract void clearFileLinks();
/*     */   
/*     */   public abstract File getFileLink(int paramInt, File paramFile);
/*     */   
/*     */   public abstract LinkFileMap getFileLinks();
/*     */   
/*     */   public abstract boolean isOurContent();
/*     */   
/*     */   public abstract void setAttribute(String paramString1, String paramString2);
/*     */   
/*     */   public abstract String getAttribute(String paramString);
/*     */   
/*     */   public abstract void setMapAttribute(String paramString, Map paramMap);
/*     */   
/*     */   public abstract Map getMapAttribute(String paramString);
/*     */   
/*     */   public abstract void setListAttribute(String paramString, String[] paramArrayOfString);
/*     */   
/*     */   public abstract String[] getListAttribute(String paramString);
/*     */   
/*     */   public abstract String getListAttribute(String paramString, int paramInt);
/*     */   
/*     */   public abstract void setIntAttribute(String paramString, int paramInt);
/*     */   
/*     */   public abstract int getIntAttribute(String paramString);
/*     */   
/*     */   public abstract void setLongAttribute(String paramString, long paramLong);
/*     */   
/*     */   public abstract long getLongAttribute(String paramString);
/*     */   
/*     */   public abstract void setBooleanAttribute(String paramString, boolean paramBoolean);
/*     */   
/*     */   public abstract boolean getBooleanAttribute(String paramString);
/*     */   
/*     */   public abstract boolean hasAttribute(String paramString);
/*     */   
/*     */   public abstract void setActive(boolean paramBoolean);
/*     */   
/*     */   public abstract void discardFluff();
/*     */   
/*     */   public abstract void save();
/*     */   
/*     */   public abstract boolean exportState(File paramFile);
/*     */   
/*     */   public abstract void delete();
/*     */   
/*     */   public abstract void addListener(DownloadManagerStateListener paramDownloadManagerStateListener);
/*     */   
/*     */   public abstract void removeListener(DownloadManagerStateListener paramDownloadManagerStateListener);
/*     */   
/*     */   public abstract boolean parameterExists(String paramString);
/*     */   
/*     */   public abstract void generateEvidence(IndentWriter paramIndentWriter);
/*     */   
/*     */   public abstract void dump(IndentWriter paramIndentWriter);
/*     */   
/*     */   public abstract void suppressStateSave(boolean paramBoolean);
/*     */   
/*     */   public abstract void addListener(DownloadManagerStateAttributeListener paramDownloadManagerStateAttributeListener, String paramString, int paramInt);
/*     */   
/*     */   public abstract void removeListener(DownloadManagerStateAttributeListener paramDownloadManagerStateAttributeListener, String paramString, int paramInt);
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/download/DownloadManagerState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */