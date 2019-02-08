package org.gudy.azureus2.plugins.messaging;

import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.peers.Peer;

public abstract interface MessageManagerListener
{
  public abstract void compatiblePeerFound(Download paramDownload, Peer paramPeer, Message paramMessage);
  
  public abstract void peerRemoved(Download paramDownload, Peer paramPeer);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/messaging/MessageManagerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */