/*    */ package org.gudy.azureus2.plugins.download;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import org.gudy.azureus2.plugins.ddb.DistributedDatabase;
/*    */ import org.gudy.azureus2.plugins.disk.DiskManager;
/*    */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*    */ import org.gudy.azureus2.plugins.download.savelocation.SaveLocationChange;
/*    */ import org.gudy.azureus2.plugins.network.RateLimiter;
/*    */ import org.gudy.azureus2.plugins.peers.PeerManager;
/*    */ import org.gudy.azureus2.plugins.tag.Tag;
/*    */ import org.gudy.azureus2.plugins.tag.Taggable;
/*    */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*    */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
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
/*    */ 
/*    */ public abstract interface Download
/*    */   extends DownloadEventNotifier, DownloadStub, Taggable
/*    */ {
/*    */   public static final int ST_WAITING = 1;
/*    */   public static final int ST_PREPARING = 2;
/*    */   public static final int ST_READY = 3;
/*    */   public static final int ST_DOWNLOADING = 4;
/*    */   public static final int ST_SEEDING = 5;
/*    */   public static final int ST_STOPPING = 6;
/*    */   public static final int ST_STOPPED = 7;
/*    */   public static final int ST_ERROR = 8;
/*    */   public static final int ST_QUEUED = 9;
/* 95 */   public static final String[] ST_NAMES = { "", "Waiting", "Preparing", "Ready", "Downloading", "Seeding", "Stopping", "Stopped", "Error", "Queued" };
/*    */   public static final int PR_HIGH_PRIORITY = 1;
/*    */   public static final int PR_LOW_PRIORITY = 2;
/*    */   public static final long FLAG_ONLY_EVER_SEEDED = 1L;
/*    */   public static final long FLAG_SCAN_INCOMPLETE_PIECES = 2L;
/*    */   public static final long FLAG_DISABLE_AUTO_FILE_MOVE = 4L;
/*    */   public static final long FLAG_MOVE_ON_COMPLETION_DONE = 8L;
/*    */   public static final long FLAG_LOW_NOISE = 16L;
/*    */   public static final long FLAG_ALLOW_PERMITTED_PEER_SOURCE_CHANGES = 32L;
/*    */   public static final long FLAG_DO_NOT_DELETE_DATA_ON_REMOVE = 64L;
/*    */   public static final long FLAG_FORCE_DIRECT_DELETE = 128L;
/*    */   public static final long FLAG_DISABLE_IP_FILTER = 256L;
/*    */   public static final long FLAG_METADATA_DOWNLOAD = 512L;
/*    */   public static final long FLAG_LIGHT_WEIGHT = 1024L;
/*    */   public static final long FLAG_ERROR_REPORTED = 2048L;
/*    */   public static final long FLAG_INITIAL_NETWORKS_SET = 4096L;
/*    */   
/*    */   public abstract int getState();
/*    */   
/*    */   public abstract int getSubState();
/*    */   
/*    */   public abstract String getErrorStateDetails();
/*    */   
/*    */   public abstract boolean getFlag(long paramLong);
/*    */   
/*    */   public abstract void setFlag(long paramLong, boolean paramBoolean);
/*    */   
/*    */   public abstract long getFlags();
/*    */   
/*    */   public abstract int getIndex();
/*    */   
/*    */   public abstract Torrent getTorrent();
/*    */   
/*    */   public abstract void initialize()
/*    */     throws DownloadException;
/*    */   
/*    */   public abstract void start()
/*    */     throws DownloadException;
/*    */   
/*    */   public abstract void stop()
/*    */     throws DownloadException;
/*    */   
/*    */   public abstract void stopAndQueue()
/*    */     throws DownloadException;
/*    */   
/*    */   public abstract void restart()
/*    */     throws DownloadException;
/*    */   
/*    */   public abstract void recheckData()
/*    */     throws DownloadException;
/*    */   
/*    */   public abstract boolean isStartStopLocked();
/*    */   
/*    */   public abstract boolean isForceStart();
/*    */   
/*    */   public abstract void setForceStart(boolean paramBoolean);
/*    */   
/*    */   /**
/*    */    * @deprecated
/*    */    */
/*    */   public abstract int getPriority();
/*    */   
/*    */   /**
/*    */    * @deprecated
/*    */    */
/*    */   public abstract void setPriority(int paramInt);
/*    */   
/*    */   /**
/*    */    * @deprecated
/*    */    */
/*    */   public abstract boolean isPriorityLocked();
/*    */   
/*    */   public abstract boolean isPaused();
/*    */   
/*    */   public abstract void pause();
/*    */   
/*    */   public abstract void resume();
/*    */   
/*    */   public abstract String getName();
/*    */   
/*    */   public abstract String getTorrentFileName();
/*    */   
/*    */   public abstract String getAttribute(TorrentAttribute paramTorrentAttribute);
/*    */   
/*    */   public abstract void setAttribute(TorrentAttribute paramTorrentAttribute, String paramString);
/*    */   
/*    */   public abstract String[] getListAttribute(TorrentAttribute paramTorrentAttribute);
/*    */   
/*    */   public abstract void setListAttribute(TorrentAttribute paramTorrentAttribute, String[] paramArrayOfString);
/*    */   
/*    */   public abstract void setMapAttribute(TorrentAttribute paramTorrentAttribute, Map paramMap);
/*    */   
/*    */   public abstract Map getMapAttribute(TorrentAttribute paramTorrentAttribute);
/*    */   
/*    */   public abstract int getIntAttribute(TorrentAttribute paramTorrentAttribute);
/*    */   
/*    */   public abstract void setIntAttribute(TorrentAttribute paramTorrentAttribute, int paramInt);
/*    */   
/*    */   public abstract long getLongAttribute(TorrentAttribute paramTorrentAttribute);
/*    */   
/*    */   public abstract void setLongAttribute(TorrentAttribute paramTorrentAttribute, long paramLong);
/*    */   
/*    */   public abstract boolean getBooleanAttribute(TorrentAttribute paramTorrentAttribute);
/*    */   
/*    */   public abstract void setBooleanAttribute(TorrentAttribute paramTorrentAttribute, boolean paramBoolean);
/*    */   
/*    */   public abstract boolean hasAttribute(TorrentAttribute paramTorrentAttribute);
/*    */   
/*    */   /**
/*    */    * @deprecated
/*    */    */
/*    */   public abstract String getCategoryName();
/*    */   
/*    */   /**
/*    */    * @deprecated
/*    */    */
/*    */   public abstract void setCategory(String paramString);
/*    */   
/*    */   public abstract List<Tag> getTags();
/*    */   
/*    */   public abstract void remove()
/*    */     throws DownloadException, DownloadRemovalVetoException;
/*    */   
/*    */   public abstract void remove(boolean paramBoolean1, boolean paramBoolean2)
/*    */     throws DownloadException, DownloadRemovalVetoException;
/*    */   
/*    */   public abstract int getPosition();
/*    */   
/*    */   public abstract long getCreationTime();
/*    */   
/*    */   public abstract void setPosition(int paramInt);
/*    */   
/*    */   public abstract void moveUp();
/*    */   
/*    */   public abstract void moveDown();
/*    */   
/*    */   public abstract void moveTo(int paramInt);
/*    */   
/*    */   public abstract boolean canBeRemoved()
/*    */     throws DownloadRemovalVetoException;
/*    */   
/*    */   public abstract void setAnnounceResult(DownloadAnnounceResult paramDownloadAnnounceResult);
/*    */   
/*    */   public abstract void setScrapeResult(DownloadScrapeResult paramDownloadScrapeResult);
/*    */   
/*    */   public abstract DownloadAnnounceResult getLastAnnounceResult();
/*    */   
/*    */   public abstract DownloadScrapeResult getLastScrapeResult();
/*    */   
/*    */   public abstract DownloadScrapeResult getAggregatedScrapeResult();
/*    */   
/*    */   public abstract DownloadActivationEvent getActivationState();
/*    */   
/*    */   public abstract DownloadStats getStats();
/*    */   
/*    */   public abstract boolean isPersistent();
/*    */   
/*    */   public abstract void setMaximumDownloadKBPerSecond(int paramInt);
/*    */   
/*    */   public abstract int getMaximumDownloadKBPerSecond();
/*    */   
/*    */   public abstract int getUploadRateLimitBytesPerSecond();
/*    */   
/*    */   public abstract void setUploadRateLimitBytesPerSecond(int paramInt);
/*    */   
/*    */   public abstract int getDownloadRateLimitBytesPerSecond();
/*    */   
/*    */   public abstract void setDownloadRateLimitBytesPerSecond(int paramInt);
/*    */   
/*    */   public abstract void addRateLimiter(RateLimiter paramRateLimiter, boolean paramBoolean);
/*    */   
/*    */   public abstract void removeRateLimiter(RateLimiter paramRateLimiter, boolean paramBoolean);
/*    */   
/*    */   public abstract boolean isComplete();
/*    */   
/*    */   public abstract boolean isComplete(boolean paramBoolean);
/*    */   
/*    */   public abstract boolean isChecking();
/*    */   
/*    */   public abstract boolean isMoving();
/*    */   
/*    */   public abstract String getSavePath();
/*    */   
/*    */   public abstract void moveDataFiles(File paramFile)
/*    */     throws DownloadException;
/*    */   
/*    */   public abstract void moveDataFiles(File paramFile, String paramString)
/*    */     throws DownloadException;
/*    */   
/*    */   public abstract void moveTorrentFile(File paramFile)
/*    */     throws DownloadException;
/*    */   
/*    */   public abstract void renameDownload(String paramString)
/*    */     throws DownloadException;
/*    */   
/*    */   public abstract PeerManager getPeerManager();
/*    */   
/*    */   public abstract DiskManager getDiskManager();
/*    */   
/*    */   public abstract DiskManagerFileInfo[] getDiskManagerFileInfo();
/*    */   
/*    */   public abstract DiskManagerFileInfo getDiskManagerFileInfo(int paramInt);
/*    */   
/*    */   public abstract int getDiskManagerFileCount();
/*    */   
/*    */   public abstract void requestTrackerAnnounce();
/*    */   
/*    */   public abstract void requestTrackerAnnounce(boolean paramBoolean);
/*    */   
/*    */   public abstract void requestTrackerScrape(boolean paramBoolean);
/*    */   
/*    */   public abstract int getSeedingRank();
/*    */   
/*    */   public abstract void setSeedingRank(int paramInt);
/*    */   
/*    */   public abstract byte[] getDownloadPeerId();
/*    */   
/*    */   public abstract boolean isMessagingEnabled();
/*    */   
/*    */   public abstract void setMessagingEnabled(boolean paramBoolean);
/*    */   
/*    */   /**
/*    */    * @deprecated
/*    */    */
/*    */   public abstract File[] calculateDefaultPaths(boolean paramBoolean);
/*    */   
/*    */   /**
/*    */    * @deprecated
/*    */    */
/*    */   public abstract boolean isInDefaultSaveDir();
/*    */   
/*    */   public abstract boolean isRemoved();
/*    */   
/*    */   public abstract boolean canMoveDataFiles();
/*    */   
/*    */   public abstract SaveLocationChange calculateDefaultDownloadLocation();
/*    */   
/*    */   public abstract void changeLocation(SaveLocationChange paramSaveLocationChange)
/*    */     throws DownloadException;
/*    */   
/*    */   public abstract Object getUserData(Object paramObject);
/*    */   
/*    */   public abstract void setUserData(Object paramObject1, Object paramObject2);
/*    */   
/*    */   public abstract void startDownload(boolean paramBoolean);
/*    */   
/*    */   public abstract void stopDownload();
/*    */   
/*    */   public abstract boolean canStubbify();
/*    */   
/*    */   public abstract DownloadStub stubbify()
/*    */     throws DownloadException, DownloadRemovalVetoException;
/*    */   
/*    */   public abstract List<DistributedDatabase> getDistributedDatabases();
/*    */   
/*    */   public abstract DiskManagerFileInfo getPrimaryFile();
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/download/Download.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */