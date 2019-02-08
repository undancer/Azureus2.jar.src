package org.gudy.azureus2.core3.tracker.server;

import java.net.URL;
import java.util.List;
import org.gudy.azureus2.core3.util.HashWrapper;

public abstract interface TRTrackerServerTorrent
{
  public abstract HashWrapper getHash();
  
  public abstract TRTrackerServerPeer[] getPeers();
  
  public abstract TRTrackerServerPeerBase[] getQueuedPeers();
  
  public abstract TRTrackerServerTorrentStats getStats();
  
  public abstract void disableCaching();
  
  public abstract void setMinBiasedPeers(int paramInt);
  
  public abstract void setEnabled(boolean paramBoolean);
  
  public abstract boolean isEnabled();
  
  public abstract void setRedirects(URL[] paramArrayOfURL);
  
  public abstract URL[] getRedirects();
  
  public abstract TRTrackerServerTorrent addLink(String paramString);
  
  public abstract void removeLink(String paramString);
  
  public abstract void addExplicitBiasedPeer(String paramString, int paramInt);
  
  public abstract void remove(TRTrackerServerPeerBase paramTRTrackerServerPeerBase);
  
  public abstract void addListener(TRTrackerServerTorrentListener paramTRTrackerServerTorrentListener);
  
  public abstract void removeListener(TRTrackerServerTorrentListener paramTRTrackerServerTorrentListener);
  
  public abstract void addPeerListener(TRTrackerServerTorrentPeerListener paramTRTrackerServerTorrentPeerListener);
  
  public abstract void removePeerListener(TRTrackerServerTorrentPeerListener paramTRTrackerServerTorrentPeerListener);
  
  public abstract void importPeers(List paramList);
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/TRTrackerServerTorrent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */