package org.gudy.azureus2.plugins.download;

import java.io.File;
import java.net.URL;
import java.util.Map;
import org.gudy.azureus2.plugins.download.savelocation.DefaultSaveLocationManager;
import org.gudy.azureus2.plugins.download.savelocation.SaveLocationManager;
import org.gudy.azureus2.plugins.torrent.Torrent;

public abstract interface DownloadManager
{
  public abstract void addDownload(File paramFile)
    throws DownloadException;
  
  public abstract void addDownload(URL paramURL)
    throws DownloadException;
  
  public abstract void addDownload(URL paramURL, boolean paramBoolean)
    throws DownloadException;
  
  public abstract void addDownload(URL paramURL1, URL paramURL2);
  
  public abstract void addDownload(URL paramURL, Map paramMap);
  
  public abstract Download addDownload(Torrent paramTorrent)
    throws DownloadException;
  
  public abstract Download addDownload(Torrent paramTorrent, File paramFile1, File paramFile2)
    throws DownloadException;
  
  public abstract Download addDownloadStopped(Torrent paramTorrent, File paramFile1, File paramFile2)
    throws DownloadException;
  
  public abstract Download addNonPersistentDownload(Torrent paramTorrent, File paramFile1, File paramFile2)
    throws DownloadException;
  
  public abstract Download addNonPersistentDownloadStopped(Torrent paramTorrent, File paramFile1, File paramFile2)
    throws DownloadException;
  
  public abstract void clearNonPersistentDownloadState(byte[] paramArrayOfByte);
  
  public abstract Download getDownload(Torrent paramTorrent);
  
  public abstract Download getDownload(byte[] paramArrayOfByte)
    throws DownloadException;
  
  public abstract Download[] getDownloads();
  
  public abstract Download[] getDownloads(boolean paramBoolean);
  
  public abstract void pauseDownloads();
  
  public abstract boolean canPauseDownloads();
  
  public abstract void resumeDownloads();
  
  public abstract boolean canResumeDownloads();
  
  public abstract void startAllDownloads();
  
  public abstract void stopAllDownloads();
  
  public abstract DownloadManagerStats getStats();
  
  public abstract boolean isSeedingOnly();
  
  public abstract void addListener(DownloadManagerListener paramDownloadManagerListener);
  
  public abstract void addListener(DownloadManagerListener paramDownloadManagerListener, boolean paramBoolean);
  
  public abstract void removeListener(DownloadManagerListener paramDownloadManagerListener, boolean paramBoolean);
  
  public abstract void removeListener(DownloadManagerListener paramDownloadManagerListener);
  
  public abstract void addDownloadWillBeAddedListener(DownloadWillBeAddedListener paramDownloadWillBeAddedListener);
  
  public abstract void removeDownloadWillBeAddedListener(DownloadWillBeAddedListener paramDownloadWillBeAddedListener);
  
  public abstract DownloadEventNotifier getGlobalDownloadEventNotifier();
  
  public abstract void setSaveLocationManager(SaveLocationManager paramSaveLocationManager);
  
  public abstract SaveLocationManager getSaveLocationManager();
  
  public abstract DefaultSaveLocationManager getDefaultSaveLocationManager();
  
  public abstract DownloadStub[] getDownloadStubs();
  
  public abstract DownloadStub lookupDownloadStub(byte[] paramArrayOfByte);
  
  public abstract int getDownloadStubCount();
  
  public abstract void addDownloadStubListener(DownloadStubListener paramDownloadStubListener, boolean paramBoolean);
  
  public abstract void removeDownloadStubListener(DownloadStubListener paramDownloadStubListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/download/DownloadManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */