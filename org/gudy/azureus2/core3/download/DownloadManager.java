package org.gudy.azureus2.core3.download;

import com.aelitis.azureus.core.networkmanager.LimitedRateGroup;
import com.aelitis.azureus.core.tag.Taggable;
import com.aelitis.azureus.core.tracker.TrackerPeerSource;
import java.io.File;
import java.util.List;
import org.gudy.azureus2.core3.disk.DiskManager;
import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
import org.gudy.azureus2.core3.disk.DiskManagerFileInfoSet;
import org.gudy.azureus2.core3.global.GlobalManager;
import org.gudy.azureus2.core3.peer.PEPeer;
import org.gudy.azureus2.core3.peer.PEPeerManager;
import org.gudy.azureus2.core3.peer.PEPiece;
import org.gudy.azureus2.core3.torrent.TOTorrent;
import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
import org.gudy.azureus2.core3.util.IndentWriter;
import org.gudy.azureus2.plugins.download.DownloadAnnounceResult;
import org.gudy.azureus2.plugins.download.DownloadScrapeResult;

public abstract interface DownloadManager
  extends Taggable
{
  public static final int STATE_START_OF_DAY = -1;
  public static final int STATE_WAITING = 0;
  public static final int STATE_INITIALIZING = 5;
  public static final int STATE_INITIALIZED = 10;
  public static final int STATE_ALLOCATING = 20;
  public static final int STATE_CHECKING = 30;
  public static final int STATE_READY = 40;
  public static final int STATE_DOWNLOADING = 50;
  public static final int STATE_FINISHING = 55;
  public static final int STATE_SEEDING = 60;
  public static final int STATE_STOPPING = 65;
  public static final int STATE_STOPPED = 70;
  public static final int STATE_CLOSED = 71;
  public static final int STATE_QUEUED = 75;
  public static final int STATE_ERROR = 100;
  public static final int WEALTH_STOPPED = 1;
  public static final int WEALTH_NO_TRACKER = 2;
  public static final int WEALTH_NO_REMOTE = 3;
  public static final int WEALTH_OK = 4;
  public static final int WEALTH_KO = 5;
  public static final int WEALTH_ERROR = 6;
  public static final int ET_NONE = 0;
  public static final int ET_OTHER = 1;
  public static final int ET_INSUFFICIENT_SPACE = 2;
  
  public abstract void initialize();
  
  public abstract int getState();
  
  public abstract int getSubState();
  
  public abstract void setStateWaiting();
  
  public abstract void setStateQueued();
  
  public abstract void startDownload();
  
  public abstract boolean canForceRecheck();
  
  public abstract void forceRecheck();
  
  public abstract void forceRecheck(ForceRecheckListener paramForceRecheckListener);
  
  public abstract void resetFile(DiskManagerFileInfo paramDiskManagerFileInfo);
  
  public abstract void recheckFile(DiskManagerFileInfo paramDiskManagerFileInfo);
  
  public abstract void setPieceCheckingEnabled(boolean paramBoolean);
  
  public abstract void stopIt(int paramInt, boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract void stopIt(int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3);
  
  public abstract boolean pause();
  
  public abstract boolean pause(long paramLong);
  
  public abstract boolean isPaused();
  
  public abstract void resume();
  
  public abstract long getAutoResumeTime();
  
  public abstract GlobalManager getGlobalManager();
  
  public abstract DiskManager getDiskManager();
  
  /**
   * @deprecated
   */
  public abstract DiskManagerFileInfo[] getDiskManagerFileInfo();
  
  public abstract DiskManagerFileInfoSet getDiskManagerFileInfoSet();
  
  public abstract int getNumFileInfos();
  
  public abstract PEPeerManager getPeerManager();
  
  public abstract DownloadManagerState getDownloadState();
  
  public abstract TOTorrent getTorrent();
  
  public abstract TRTrackerAnnouncer getTrackerClient();
  
  public abstract void requestTrackerAnnounce(boolean paramBoolean);
  
  public abstract void requestTrackerScrape(boolean paramBoolean);
  
  public abstract TRTrackerScraperResponse getTrackerScrapeResponse();
  
  public abstract List<TRTrackerScraperResponse> getGoodTrackerScrapeResponses();
  
  public abstract void setTrackerScrapeResponse(TRTrackerScraperResponse paramTRTrackerScraperResponse);
  
  public abstract String getDisplayName();
  
  public abstract String getInternalName();
  
  public abstract long getSize();
  
  public abstract String getTorrentFileName();
  
  public abstract void setTorrentFileName(String paramString);
  
  public abstract File getAbsoluteSaveLocation();
  
  public abstract File getSaveLocation();
  
  public abstract void setTorrentSaveDir(String paramString);
  
  public abstract void setTorrentSaveDir(String paramString1, String paramString2);
  
  public abstract boolean isForceStart();
  
  public abstract void setForceStart(boolean paramBoolean);
  
  public abstract boolean isPersistent();
  
  public abstract boolean isDownloadComplete(boolean paramBoolean);
  
  public abstract String getTrackerStatus();
  
  public abstract int getTrackerTime();
  
  public abstract String getTorrentComment();
  
  public abstract String getTorrentCreatedBy();
  
  public abstract long getTorrentCreationDate();
  
  public abstract int getNbPieces();
  
  public abstract String getPieceLength();
  
  public abstract int getNbSeeds();
  
  public abstract int getNbPeers();
  
  /**
   * @deprecated
   */
  public abstract boolean filesExist();
  
  public abstract boolean filesExist(boolean paramBoolean);
  
  public abstract String getErrorDetails();
  
  public abstract int getErrorType();
  
  public abstract DownloadManagerStats getStats();
  
  public abstract int getPosition();
  
  public abstract void setPosition(int paramInt);
  
  public abstract boolean getAssumedComplete();
  
  public abstract boolean requestAssumedCompleteMode();
  
  public abstract int getHealthStatus();
  
  public abstract int getNATStatus();
  
  public abstract void saveResumeData();
  
  public abstract void saveDownload();
  
  /**
   * @deprecated
   */
  public abstract Object getData(String paramString);
  
  /**
   * @deprecated
   */
  public abstract void setData(String paramString, Object paramObject);
  
  public abstract Object getUserData(Object paramObject);
  
  public abstract void setUserData(Object paramObject1, Object paramObject2);
  
  public abstract boolean isDataAlreadyAllocated();
  
  public abstract void setDataAlreadyAllocated(boolean paramBoolean);
  
  public abstract void setSeedingRank(int paramInt);
  
  public abstract int getSeedingRank();
  
  public abstract String isSwarmMerging();
  
  public abstract void setMaxUploads(int paramInt);
  
  public abstract int getMaxUploads();
  
  public abstract void updateAutoUploadPriority(Object paramObject, boolean paramBoolean);
  
  public abstract int getEffectiveMaxUploads();
  
  public abstract int getEffectiveUploadRateLimitBytesPerSecond();
  
  public abstract void setCryptoLevel(int paramInt);
  
  public abstract int getCryptoLevel();
  
  public abstract void moveDataFiles(File paramFile)
    throws DownloadManagerException;
  
  public abstract void moveDataFilesLive(File paramFile)
    throws DownloadManagerException;
  
  public abstract void copyDataFiles(File paramFile)
    throws DownloadManagerException;
  
  public abstract void renameDownload(String paramString)
    throws DownloadManagerException;
  
  public abstract void moveDataFiles(File paramFile, String paramString)
    throws DownloadManagerException;
  
  public abstract void moveTorrentFile(File paramFile)
    throws DownloadManagerException;
  
  public abstract boolean isInDefaultSaveDir();
  
  public abstract long getCreationTime();
  
  public abstract void setCreationTime(long paramLong);
  
  public abstract void setAnnounceResult(DownloadAnnounceResult paramDownloadAnnounceResult);
  
  public abstract void setScrapeResult(DownloadScrapeResult paramDownloadScrapeResult);
  
  public abstract boolean isUnauthorisedOnTracker();
  
  public abstract boolean isTrackerError();
  
  public abstract int getExtendedMessagingMode();
  
  public abstract void destroy(boolean paramBoolean);
  
  public abstract boolean isDestroyed();
  
  public abstract PEPiece[] getCurrentPieces();
  
  public abstract PEPeer[] getCurrentPeers();
  
  public abstract List<TrackerPeerSource> getTrackerPeerSources();
  
  public abstract boolean seedPieceRecheck();
  
  public abstract void addRateLimiter(LimitedRateGroup paramLimitedRateGroup, boolean paramBoolean);
  
  public abstract LimitedRateGroup[] getRateLimiters(boolean paramBoolean);
  
  public abstract void removeRateLimiter(LimitedRateGroup paramLimitedRateGroup, boolean paramBoolean);
  
  public abstract void addListener(DownloadManagerListener paramDownloadManagerListener, boolean paramBoolean);
  
  public abstract void addListener(DownloadManagerListener paramDownloadManagerListener);
  
  public abstract void removeListener(DownloadManagerListener paramDownloadManagerListener);
  
  public abstract void addTrackerListener(DownloadManagerTrackerListener paramDownloadManagerTrackerListener);
  
  public abstract void removeTrackerListener(DownloadManagerTrackerListener paramDownloadManagerTrackerListener);
  
  public abstract void addPeerListener(DownloadManagerPeerListener paramDownloadManagerPeerListener);
  
  public abstract void addPeerListener(DownloadManagerPeerListener paramDownloadManagerPeerListener, boolean paramBoolean);
  
  public abstract void removePeerListener(DownloadManagerPeerListener paramDownloadManagerPeerListener);
  
  public abstract void addPieceListener(DownloadManagerPieceListener paramDownloadManagerPieceListener);
  
  public abstract void addPieceListener(DownloadManagerPieceListener paramDownloadManagerPieceListener, boolean paramBoolean);
  
  public abstract void removePieceListener(DownloadManagerPieceListener paramDownloadManagerPieceListener);
  
  public abstract void addDiskListener(DownloadManagerDiskListener paramDownloadManagerDiskListener);
  
  public abstract void removeDiskListener(DownloadManagerDiskListener paramDownloadManagerDiskListener);
  
  public abstract int getActivationCount();
  
  public abstract void addActivationListener(DownloadManagerActivationListener paramDownloadManagerActivationListener);
  
  public abstract void removeActivationListener(DownloadManagerActivationListener paramDownloadManagerActivationListener);
  
  public abstract void addTPSListener(DownloadManagerTPSListener paramDownloadManagerTPSListener);
  
  public abstract void removeTPSListener(DownloadManagerTPSListener paramDownloadManagerTPSListener);
  
  public abstract void generateEvidence(IndentWriter paramIndentWriter);
  
  public abstract int[] getStorageType(DiskManagerFileInfo[] paramArrayOfDiskManagerFileInfo);
  
  public abstract boolean canMoveDataFiles();
  
  public abstract void rename(String paramString)
    throws DownloadManagerException;
  
  public abstract void renameTorrent(String paramString)
    throws DownloadManagerException;
  
  public abstract void renameTorrentSafe(String paramString)
    throws DownloadManagerException;
  
  public abstract void moveTorrentFile(File paramFile, String paramString)
    throws DownloadManagerException;
  
  public abstract void setTorrentFile(File paramFile, String paramString)
    throws DownloadManagerException;
  
  public abstract void fireGlobalManagerEvent(int paramInt);
  
  public abstract void setFilePriorities(DiskManagerFileInfo[] paramArrayOfDiskManagerFileInfo, int paramInt);
  
  public abstract void requestAttention();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/download/DownloadManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */