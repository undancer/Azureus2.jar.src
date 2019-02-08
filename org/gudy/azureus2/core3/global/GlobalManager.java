package org.gudy.azureus2.core3.global;

import com.aelitis.azureus.core.AzureusCoreComponent;
import com.aelitis.azureus.core.tag.TaggableResolver;
import java.util.List;
import java.util.Map;
import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.core3.download.DownloadManagerInitialisationAdapter;
import org.gudy.azureus2.core3.torrent.TOTorrent;
import org.gudy.azureus2.core3.tracker.client.TRTrackerScraper;
import org.gudy.azureus2.core3.util.HashWrapper;
import org.gudy.azureus2.plugins.dht.mainline.MainlineDHTProvider;

public abstract interface GlobalManager
  extends AzureusCoreComponent, TaggableResolver
{
  public abstract DownloadManager addDownloadManager(String paramString1, String paramString2);
  
  public abstract DownloadManager addDownloadManager(String paramString1, byte[] paramArrayOfByte, String paramString2, int paramInt, boolean paramBoolean);
  
  public abstract DownloadManager addDownloadManager(String paramString1, byte[] paramArrayOfByte, String paramString2, String paramString3, int paramInt, boolean paramBoolean1, boolean paramBoolean2, DownloadManagerInitialisationAdapter paramDownloadManagerInitialisationAdapter);
  
  public abstract DownloadManager addDownloadManager(String paramString1, byte[] paramArrayOfByte, String paramString2, int paramInt, boolean paramBoolean1, boolean paramBoolean2, DownloadManagerInitialisationAdapter paramDownloadManagerInitialisationAdapter);
  
  public abstract void removeDownloadManager(DownloadManager paramDownloadManager)
    throws GlobalManagerDownloadRemovalVetoException;
  
  public abstract void canDownloadManagerBeRemoved(DownloadManager paramDownloadManager, boolean paramBoolean1, boolean paramBoolean2)
    throws GlobalManagerDownloadRemovalVetoException;
  
  public abstract List<DownloadManager> getDownloadManagers();
  
  public abstract DownloadManager getDownloadManager(TOTorrent paramTOTorrent);
  
  public abstract DownloadManager getDownloadManager(HashWrapper paramHashWrapper);
  
  public abstract TRTrackerScraper getTrackerScraper();
  
  public abstract GlobalManagerStats getStats();
  
  public abstract void stopGlobalManager();
  
  public abstract void stopAllDownloads();
  
  public abstract void startAllDownloads();
  
  public abstract void pauseDownloads();
  
  public abstract void pauseDownloadsForPeriod(int paramInt);
  
  public abstract int getPauseDownloadPeriodRemaining();
  
  public abstract boolean canPauseDownloads();
  
  public abstract void resumeDownloads();
  
  public abstract boolean resumeDownloads(boolean paramBoolean);
  
  public abstract boolean canResumeDownloads();
  
  public abstract boolean resumingDownload(DownloadManager paramDownloadManager);
  
  public abstract boolean pauseDownload(DownloadManager paramDownloadManager);
  
  public abstract void resumeDownload(DownloadManager paramDownloadManager);
  
  public abstract void clearNonPersistentDownloadState(byte[] paramArrayOfByte);
  
  public abstract boolean isPaused(DownloadManager paramDownloadManager);
  
  public abstract String isSwarmMerging(DownloadManager paramDownloadManager);
  
  public abstract boolean isSeedingOnly();
  
  public abstract boolean isPotentiallySeedingOnly();
  
  /**
   * @deprecated
   */
  public abstract int getIndexOf(DownloadManager paramDownloadManager);
  
  public abstract int downloadManagerCount(boolean paramBoolean);
  
  public abstract boolean isMoveableDown(DownloadManager paramDownloadManager);
  
  public abstract boolean isMoveableUp(DownloadManager paramDownloadManager);
  
  public abstract void moveTop(DownloadManager[] paramArrayOfDownloadManager);
  
  public abstract void moveUp(DownloadManager paramDownloadManager);
  
  public abstract void moveDown(DownloadManager paramDownloadManager);
  
  public abstract void moveEnd(DownloadManager[] paramArrayOfDownloadManager);
  
  public abstract void moveTo(DownloadManager paramDownloadManager, int paramInt);
  
  public abstract void fixUpDownloadManagerPositions();
  
  public abstract void addListener(GlobalManagerListener paramGlobalManagerListener);
  
  public abstract void removeListener(GlobalManagerListener paramGlobalManagerListener);
  
  public abstract void addDownloadWillBeRemovedListener(GlobalManagerDownloadWillBeRemovedListener paramGlobalManagerDownloadWillBeRemovedListener);
  
  public abstract void removeDownloadWillBeRemovedListener(GlobalManagerDownloadWillBeRemovedListener paramGlobalManagerDownloadWillBeRemovedListener);
  
  public abstract int getNATStatus();
  
  public abstract void addDownloadManagerInitialisationAdapter(DownloadManagerInitialisationAdapter paramDownloadManagerInitialisationAdapter);
  
  public abstract void removeDownloadManagerInitialisationAdapter(DownloadManagerInitialisationAdapter paramDownloadManagerInitialisationAdapter);
  
  public abstract void addEventListener(GlobalManagerEventListener paramGlobalManagerEventListener);
  
  public abstract void removeEventListener(GlobalManagerEventListener paramGlobalManagerEventListener);
  
  public abstract void fireGlobalManagerEvent(int paramInt, DownloadManager paramDownloadManager);
  
  public abstract void loadExistingTorrentsNow(boolean paramBoolean);
  
  public abstract void addListener(GlobalManagerListener paramGlobalManagerListener, boolean paramBoolean);
  
  public abstract void removeDownloadManager(DownloadManager paramDownloadManager, boolean paramBoolean1, boolean paramBoolean2)
    throws GlobalManagerDownloadRemovalVetoException;
  
  public abstract void setMainlineDHTProvider(MainlineDHTProvider paramMainlineDHTProvider);
  
  public abstract MainlineDHTProvider getMainlineDHTProvider();
  
  public abstract void statsRequest(Map paramMap1, Map paramMap2);
  
  public abstract boolean contains(DownloadManager paramDownloadManager);
  
  public abstract void saveState();
  
  public abstract Map exportDownloadStateToMap(DownloadManager paramDownloadManager);
  
  public abstract DownloadManager importDownloadStateFromMap(Map paramMap);
  
  public abstract Object getDownloadHistoryManager();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/global/GlobalManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */