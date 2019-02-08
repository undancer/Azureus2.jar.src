package org.gudy.azureus2.plugins.download;

import org.gudy.azureus2.plugins.peers.PeerManager;

public abstract interface DownloadPeerListener
{
  public abstract void peerManagerAdded(Download paramDownload, PeerManager paramPeerManager);
  
  public abstract void peerManagerRemoved(Download paramDownload, PeerManager paramPeerManager);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/download/DownloadPeerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */