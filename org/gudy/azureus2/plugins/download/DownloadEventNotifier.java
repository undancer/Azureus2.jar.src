package org.gudy.azureus2.plugins.download;

import org.gudy.azureus2.plugins.torrent.TorrentAttribute;

public abstract interface DownloadEventNotifier
{
  public abstract void addListener(DownloadListener paramDownloadListener);
  
  public abstract void removeListener(DownloadListener paramDownloadListener);
  
  public abstract void addTrackerListener(DownloadTrackerListener paramDownloadTrackerListener);
  
  public abstract void addTrackerListener(DownloadTrackerListener paramDownloadTrackerListener, boolean paramBoolean);
  
  public abstract void removeTrackerListener(DownloadTrackerListener paramDownloadTrackerListener);
  
  public abstract void addDownloadWillBeRemovedListener(DownloadWillBeRemovedListener paramDownloadWillBeRemovedListener);
  
  public abstract void removeDownloadWillBeRemovedListener(DownloadWillBeRemovedListener paramDownloadWillBeRemovedListener);
  
  public abstract void addActivationListener(DownloadActivationListener paramDownloadActivationListener);
  
  public abstract void removeActivationListener(DownloadActivationListener paramDownloadActivationListener);
  
  public abstract void addPeerListener(DownloadPeerListener paramDownloadPeerListener);
  
  public abstract void removePeerListener(DownloadPeerListener paramDownloadPeerListener);
  
  /**
   * @deprecated
   */
  public abstract void addPropertyListener(DownloadPropertyListener paramDownloadPropertyListener);
  
  public abstract void removePropertyListener(DownloadPropertyListener paramDownloadPropertyListener);
  
  public abstract void addAttributeListener(DownloadAttributeListener paramDownloadAttributeListener, TorrentAttribute paramTorrentAttribute, int paramInt);
  
  public abstract void removeAttributeListener(DownloadAttributeListener paramDownloadAttributeListener, TorrentAttribute paramTorrentAttribute, int paramInt);
  
  public abstract void addCompletionListener(DownloadCompletionListener paramDownloadCompletionListener);
  
  public abstract void removeCompletionListener(DownloadCompletionListener paramDownloadCompletionListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/download/DownloadEventNotifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */