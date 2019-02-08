package org.gudy.azureus2.core3.download;

import org.gudy.azureus2.core3.peer.PEPeer;
import org.gudy.azureus2.core3.peer.PEPeerManager;

public abstract interface DownloadManagerPeerListener
{
  public abstract void peerManagerWillBeAdded(PEPeerManager paramPEPeerManager);
  
  public abstract void peerManagerAdded(PEPeerManager paramPEPeerManager);
  
  public abstract void peerManagerRemoved(PEPeerManager paramPEPeerManager);
  
  public abstract void peerAdded(PEPeer paramPEPeer);
  
  public abstract void peerRemoved(PEPeer paramPEPeer);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/download/DownloadManagerPeerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */