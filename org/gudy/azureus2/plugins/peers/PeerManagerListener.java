package org.gudy.azureus2.plugins.peers;

/**
 * @deprecated
 */
public abstract interface PeerManagerListener
{
  public abstract void peerAdded(PeerManager paramPeerManager, Peer paramPeer);
  
  public abstract void peerRemoved(PeerManager paramPeerManager, Peer paramPeer);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/peers/PeerManagerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */