package org.gudy.azureus2.core3.peer;

import com.aelitis.azureus.core.peermanager.peerdb.PeerItem;

public abstract interface PEPeerManagerListener
{
  public abstract void peerAdded(PEPeerManager paramPEPeerManager, PEPeer paramPEPeer);
  
  public abstract void peerRemoved(PEPeerManager paramPEPeerManager, PEPeer paramPEPeer);
  
  public abstract void pieceAdded(PEPeerManager paramPEPeerManager, PEPiece paramPEPiece, PEPeer paramPEPeer);
  
  public abstract void pieceRemoved(PEPeerManager paramPEPeerManager, PEPiece paramPEPiece);
  
  public abstract void peerDiscovered(PEPeerManager paramPEPeerManager, PeerItem paramPeerItem, PEPeer paramPEPeer);
  
  public abstract void peerSentBadData(PEPeerManager paramPEPeerManager, PEPeer paramPEPeer, int paramInt);
  
  public abstract void pieceCorrupted(PEPeerManager paramPEPeerManager, int paramInt);
  
  public abstract void destroyed();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/PEPeerManagerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */