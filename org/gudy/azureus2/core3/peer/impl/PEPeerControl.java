package org.gudy.azureus2.core3.peer.impl;

import com.aelitis.azureus.core.peermanager.peerdb.PeerItem;
import java.util.Map;
import org.gudy.azureus2.core3.peer.PEPeer;
import org.gudy.azureus2.core3.peer.PEPeerManager;

public abstract interface PEPeerControl
  extends PEPeerManager
{
  public abstract boolean validateReadRequest(PEPeerTransport paramPEPeerTransport, int paramInt1, int paramInt2, int paramInt3);
  
  public abstract boolean validateHintRequest(PEPeerTransport paramPEPeerTransport, int paramInt1, int paramInt2, int paramInt3);
  
  public abstract void havePiece(int paramInt1, int paramInt2, PEPeer paramPEPeer);
  
  public abstract void updateSuperSeedPiece(PEPeer paramPEPeer, int paramInt);
  
  public abstract boolean isPrivateTorrent();
  
  public abstract int getExtendedMessagingMode();
  
  public abstract boolean isPeerExchangeEnabled();
  
  public abstract byte[][] getSecrets(int paramInt);
  
  public abstract int getUploadPriority();
  
  public abstract int getHiddenPiece();
  
  public abstract void addPeerTransport(PEPeerTransport paramPEPeerTransport);
  
  public abstract int getConnectTimeout(int paramInt);
  
  public abstract int[] getMaxConnections();
  
  public abstract boolean doOptimisticDisconnect(boolean paramBoolean1, boolean paramBoolean2, String paramString);
  
  public abstract int getNbActivePieces();
  
  public abstract int getNbPeersStalledPendingLoad();
  
  public abstract void incNbPeersSnubbed();
  
  public abstract void decNbPeersSnubbed();
  
  public abstract void setNbPeersSnubbed(int paramInt);
  
  public abstract int getNbPeersSnubbed();
  
  public abstract void badPieceReported(PEPeerTransport paramPEPeerTransport, int paramInt);
  
  public abstract boolean isFastExtensionPermitted(PEPeerTransport paramPEPeerTransport);
  
  public abstract void reportBadFastExtensionUse(PEPeerTransport paramPEPeerTransport);
  
  public abstract void statsRequest(PEPeerTransport paramPEPeerTransport, Map paramMap);
  
  public abstract void statsReply(PEPeerTransport paramPEPeerTransport, Map paramMap);
  
  public abstract boolean isRTA();
  
  public abstract void peerDiscovered(PEPeerTransport paramPEPeerTransport, PeerItem paramPeerItem);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/impl/PEPeerControl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */