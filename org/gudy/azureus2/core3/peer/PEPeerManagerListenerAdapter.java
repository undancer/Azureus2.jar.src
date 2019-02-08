package org.gudy.azureus2.core3.peer;

import com.aelitis.azureus.core.peermanager.peerdb.PeerItem;

public class PEPeerManagerListenerAdapter
  implements PEPeerManagerListener
{
  public void peerAdded(PEPeerManager manager, PEPeer peer) {}
  
  public void peerRemoved(PEPeerManager manager, PEPeer peer) {}
  
  public void pieceAdded(PEPeerManager manager, PEPiece piece, PEPeer for_peer) {}
  
  public void pieceRemoved(PEPeerManager manager, PEPiece piece) {}
  
  public void peerDiscovered(PEPeerManager manager, PeerItem peer, PEPeer finder) {}
  
  public void peerSentBadData(PEPeerManager manager, PEPeer peer, int piece_number) {}
  
  public void pieceCorrupted(PEPeerManager manager, int piece_number) {}
  
  public void destroyed() {}
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/PEPeerManagerListenerAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */