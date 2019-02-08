package org.gudy.azureus2.plugins.peers;

import java.util.Map;
import org.gudy.azureus2.plugins.disk.DiskManager;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.download.DownloadException;
import org.gudy.azureus2.plugins.utils.PooledByteBuffer;

public abstract interface PeerManager
{
  public abstract Download getDownload()
    throws DownloadException;
  
  public abstract void addPeer(Peer paramPeer);
  
  public abstract void addPeer(String paramString, int paramInt);
  
  public abstract void addPeer(String paramString, int paramInt, boolean paramBoolean);
  
  public abstract void addPeer(String paramString, int paramInt1, int paramInt2, boolean paramBoolean);
  
  public abstract void addPeer(String paramString, int paramInt1, int paramInt2, boolean paramBoolean, Map<Object, Object> paramMap);
  
  public abstract void peerDiscovered(String paramString1, String paramString2, int paramInt1, int paramInt2, boolean paramBoolean);
  
  public abstract void removePeer(Peer paramPeer);
  
  public abstract Peer[] getPeers();
  
  public abstract Peer[] getPeers(String paramString);
  
  public abstract PeerDescriptor[] getPendingPeers();
  
  public abstract PeerDescriptor[] getPendingPeers(String paramString);
  
  public abstract DiskManager getDiskManager();
  
  public abstract PeerManagerStats getStats();
  
  public abstract boolean isSeeding();
  
  public abstract boolean isSuperSeeding();
  
  public abstract PeerStats createPeerStats(Peer paramPeer);
  
  public abstract void requestComplete(PeerReadRequest paramPeerReadRequest, PooledByteBuffer paramPooledByteBuffer, Peer paramPeer);
  
  public abstract void requestCancelled(PeerReadRequest paramPeerReadRequest, Peer paramPeer);
  
  public abstract Piece[] getPieces();
  
  public abstract int getUploadRateLimitBytesPerSecond();
  
  public abstract int getDownloadRateLimitBytesPerSecond();
  
  public abstract void addListener(PeerManagerListener paramPeerManagerListener);
  
  public abstract void removeListener(PeerManagerListener paramPeerManagerListener);
  
  public abstract void addListener(PeerManagerListener2 paramPeerManagerListener2);
  
  public abstract void removeListener(PeerManagerListener2 paramPeerManagerListener2);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/peers/PeerManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */