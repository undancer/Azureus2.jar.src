package org.gudy.azureus2.core3.tracker.client;

import com.aelitis.azureus.core.tracker.TrackerPeerSource;
import java.net.URL;
import java.util.Map;
import org.gudy.azureus2.core3.torrent.TOTorrent;
import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
import org.gudy.azureus2.core3.util.IndentWriter;
import org.gudy.azureus2.plugins.download.DownloadAnnounceResult;

public abstract interface TRTrackerAnnouncer
{
  public static final byte AZ_TRACKER_VERSION_1 = 1;
  public static final byte AZ_TRACKER_VERSION_2 = 2;
  public static final byte AZ_TRACKER_VERSION_3 = 3;
  public static final byte AZ_TRACKER_VERSION_CURRENT = 3;
  public static final int REFRESH_MINIMUM_SECS = 60;
  public static final int DEFAULT_PEERS_TO_CACHE = 512;
  public static final int TS_INITIALISED = 1;
  public static final int TS_DOWNLOADING = 2;
  public static final int TS_COMPLETED = 3;
  public static final int TS_STOPPED = 4;
  
  public abstract void setAnnounceDataProvider(TRTrackerAnnouncerDataProvider paramTRTrackerAnnouncerDataProvider);
  
  public abstract TOTorrent getTorrent();
  
  public abstract URL getTrackerURL();
  
  public abstract void setTrackerURL(URL paramURL);
  
  public abstract void resetTrackerUrl(boolean paramBoolean);
  
  public abstract void setIPOverride(String paramString);
  
  public abstract void clearIPOverride();
  
  public abstract byte[] getPeerId();
  
  public abstract void setRefreshDelayOverrides(int paramInt);
  
  public abstract int getTimeUntilNextUpdate();
  
  public abstract int getLastUpdateTime();
  
  public abstract void update(boolean paramBoolean);
  
  public abstract void complete(boolean paramBoolean);
  
  public abstract void stop(boolean paramBoolean);
  
  public abstract void destroy();
  
  public abstract int getStatus();
  
  public abstract boolean isManual();
  
  public abstract String getStatusString();
  
  public abstract TRTrackerAnnouncer getBestAnnouncer();
  
  public abstract TRTrackerAnnouncerResponse getLastResponse();
  
  public abstract Map getTrackerResponseCache();
  
  public abstract void setTrackerResponseCache(Map paramMap);
  
  public abstract void removeFromTrackerResponseCache(String paramString, int paramInt);
  
  public abstract TrackerPeerSource getTrackerPeerSource(TOTorrentAnnounceURLSet paramTOTorrentAnnounceURLSet);
  
  public abstract TrackerPeerSource getCacheTrackerPeerSource();
  
  public abstract void refreshListeners();
  
  public abstract void setAnnounceResult(DownloadAnnounceResult paramDownloadAnnounceResult);
  
  public abstract void addListener(TRTrackerAnnouncerListener paramTRTrackerAnnouncerListener);
  
  public abstract void removeListener(TRTrackerAnnouncerListener paramTRTrackerAnnouncerListener);
  
  public abstract void generateEvidence(IndentWriter paramIndentWriter);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/TRTrackerAnnouncer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */