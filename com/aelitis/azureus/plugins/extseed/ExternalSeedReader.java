package com.aelitis.azureus.plugins.extseed;

import java.net.URL;
import java.util.List;
import org.gudy.azureus2.plugins.peers.Peer;
import org.gudy.azureus2.plugins.peers.PeerManager;
import org.gudy.azureus2.plugins.peers.PeerReadRequest;
import org.gudy.azureus2.plugins.torrent.Torrent;

public abstract interface ExternalSeedReader
{
  public abstract Torrent getTorrent();
  
  public abstract String getName();
  
  public abstract String getType();
  
  public abstract String getStatus();
  
  public abstract boolean isTransient();
  
  public abstract boolean isPermanentlyUnavailable();
  
  public abstract URL getURL();
  
  public abstract String getIP();
  
  public abstract int getPort();
  
  public abstract boolean isActive();
  
  public abstract boolean sameAs(ExternalSeedReader paramExternalSeedReader);
  
  public abstract boolean checkActivation(PeerManager paramPeerManager, Peer paramPeer);
  
  public abstract void addRequests(List<PeerReadRequest> paramList);
  
  public abstract void cancelRequest(PeerReadRequest paramPeerReadRequest);
  
  public abstract int getMaximumNumberOfRequests();
  
  public abstract void calculatePriorityOffsets(PeerManager paramPeerManager, int[] paramArrayOfInt);
  
  public abstract int[] getPriorityOffsets();
  
  public abstract void cancelAllRequests();
  
  public abstract int getRequestCount();
  
  public abstract List<PeerReadRequest> getExpiredRequests();
  
  public abstract List<PeerReadRequest> getRequests();
  
  public abstract int readBytes(int paramInt);
  
  public abstract int getPercentDoneOfCurrentIncomingRequest();
  
  public abstract int[] getOutgoingRequestedPieceNumbers();
  
  public abstract int getOutgoingRequestCount();
  
  public abstract byte[] read(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws ExternalSeedException;
  
  public abstract void deactivate(String paramString);
  
  public abstract void addListener(ExternalSeedReaderListener paramExternalSeedReaderListener);
  
  public abstract void removeListener(ExternalSeedReaderListener paramExternalSeedReaderListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/extseed/ExternalSeedReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */